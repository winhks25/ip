package stewie.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import stewie.model.Deadline;
import stewie.model.Event;
import stewie.model.Task;
import stewie.model.ToDo;

/** Loads validated task records and replaces saved data only after a complete write succeeds. */
public class Storage {
    private final Path storagePath;
    // Retains the loaded bytes to detect external edits before replacing the file.
    private byte[] savedContent;
    private boolean isReadOnly;
    private String loadWarning = "";

    /** Creates storage at the default task file location. */
    public Storage() {
        this(Path.of("data/stewie.txt"));
    }

    /**
     * Creates storage at a supplied path, allowing tests to use isolated files.
     *
     * @param storagePath Path of the task file.
     */
    public Storage(Path storagePath) {
        this.storagePath = storagePath.toAbsolutePath();
    }

    /**
     * Returns any startup warning so both console and graphical interfaces can display it.
     *
     * @return Warning, or an empty string for a clean load.
     */
    public String getLoadWarning() {
        return loadWarning;
    }

    /**
     * Saves the proposed list without truncating the existing task file on a failed write.
     *
     * @param tasks Complete proposed task list.
     * @throws StorageException If saving is unsafe or fails; the caller must retain its old task list.
     */
    public void saveToDisk(ArrayList<Task> tasks) {
        try {
            prepareForSave();
            byte[] content = serializeTasks(tasks);
            writeAtomically(content);
            savedContent = content;
        } catch (IOException | SecurityException exception) {
            throw new StorageException("Unable to save tasks. No changes were applied. "
                    + "Check the task file, permissions, free space, and support for atomic file replacement.");
        }
    }

    /** Rejects unsafe writes before creating the storage directory. */
    private void prepareForSave() throws IOException {
        if (isReadOnly) {
            throw new StorageException("Tasks are read-only. Back up and repair the task file, then restart.");
        }
        if (Files.isSymbolicLink(storagePath) || !Arrays.equals(savedContent, readExistingFile())) {
            throw new StorageException("The task file changed outside Stewie. Restart before making changes.");
        }
        if (Files.exists(storagePath, LinkOption.NOFOLLOW_LINKS) && !Files.isWritable(storagePath)) {
            throw new IOException("Task file is not writable");
        }
        Files.createDirectories(storagePath.getParent());
    }

    /** Encodes all task records using the same UTF-8 format as the loader. */
    private byte[] serializeTasks(ArrayList<Task> tasks) {
        String content = tasks.stream().map(Storage::serializeTask).collect(Collectors.joining("\n"));
        return content.getBytes(StandardCharsets.UTF_8);
    }

    /** Replaces the original only after writing a complete temporary file with matching permissions. */
    private void writeAtomically(byte[] content) throws IOException {
        Path temporaryFile = Files.createTempFile(storagePath.getParent(), ".stewie-", ".tmp");
        try {
            Files.write(temporaryFile, content);
            copyPermissions(temporaryFile);
            Files.move(temporaryFile, storagePath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Retains POSIX permissions where an existing task file and filesystem support them. */
    private void copyPermissions(Path temporaryFile) throws IOException {
        if (Files.exists(storagePath) && Files.getFileStore(storagePath).supportsFileAttributeView("posix")) {
            Files.setPosixFilePermissions(temporaryFile, Files.getPosixFilePermissions(storagePath));
        }
    }

    /** Removes an unused temporary file without masking the outcome of the save. */
    private void deleteTemporaryFile(Path temporaryFile) {
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException exception) {
            // A leftover temporary file is safer than touching the original data.
        }
    }

    /**
     * Loads valid records and protects the original file if any record cannot be trusted.
     *
     * @return Valid tasks in their original order, including records after a malformed line.
     */
    public ArrayList<Task> loadFromDisk() {
        ArrayList<Task> tasks = new ArrayList<>();
        resetLoadState();
        try {
            savedContent = readTaskFile();
            if (savedContent != null) {
                ArrayList<String> invalidLines = loadRecords(decodeLines(savedContent), tasks);
                warnAboutInvalidRecords(invalidLines);
            }
        } catch (IOException | SecurityException exception) {
            warnAboutUnreadableFile();
        }
        return tasks;
    }

    /** Clears warnings before attempting a fresh load. */
    private void resetLoadState() {
        isReadOnly = false;
        loadWarning = "";
    }

    /** Rejects symbolic links before retaining a snapshot of the existing file. */
    private byte[] readTaskFile() throws IOException {
        if (Files.isSymbolicLink(storagePath)) {
            throw new IOException("Symbolic links are not supported for task files");
        }
        return readExistingFile();
    }

    /** Decodes UTF-8 strictly and retains empty records so corruption remains visible. */
    private String[] decodeLines(byte[] content) throws IOException {
        String decoded = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(content)).toString();
        return decoded.split("\\R", -1);
    }

    /** Collects valid tasks and the one-based line numbers of rejected records. */
    private ArrayList<String> loadRecords(String[] lines, ArrayList<Task> tasks) {
        ArrayList<String> invalidLines = new ArrayList<>();
        for (int index = 0; index < lines.length; index++) {
            if (index == lines.length - 1 && lines[index].isEmpty()) {
                continue;
            }
            try {
                addUniqueRecord(lines[index], tasks);
            } catch (IllegalArgumentException exception) {
                invalidLines.add(Integer.toString(index + 1));
            }
        }
        return invalidLines;
    }

    /** Adds a parsed record only when its task details are not already present. */
    private void addUniqueRecord(String line, ArrayList<Task> tasks) {
        Task task = parseTask(line);
        if (tasks.stream().anyMatch(task::hasSameDetails)) {
            throw new IllegalArgumentException("Duplicate task");
        }
        tasks.add(task);
    }

    /** Protects damaged input from overwrites while keeping its valid tasks available. */
    private void warnAboutInvalidRecords(ArrayList<String> invalidLines) {
        if (!invalidLines.isEmpty()) {
            isReadOnly = true;
            loadWarning = "Storage warning: Invalid or duplicate records at lines "
                    + String.join(", ", invalidLines) + ". Valid tasks are available read-only. "
                    + "Back up and repair the task file, then restart.";
        }
    }

    /** Protects unread data and explains how the user can recover access. */
    private void warnAboutUnreadableFile() {
        isReadOnly = true;
        loadWarning = "Storage warning: Unable to read the task file. Tasks are read-only. "
                + "Check the file path, permissions, and UTF-8 content, then restart.";
    }

    /** Distinguishes a missing file from other I/O failures, which must never permit overwriting unread data. */
    private byte[] readExistingFile() throws IOException {
        try {
            return Files.readAllBytes(storagePath);
        } catch (NoSuchFileException exception) {
            return null;
        }
    }

    /** Validates the entire storage record, including its completion status and exact field count. */
    private static Task parseTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3 || !(parts[1].equals("0") || parts[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task status or field count");
        }
        Task task;
        if (parts[0].equals("T") && parts.length == 3) {
            task = new ToDo(parts[2]);
        } else if (parts[0].equals("D") && parts.length == 4) {
            task = new Deadline(parts[2], parts[3]);
        } else if (parts[0].equals("E") && parts.length == 5) {
            task = new Event(parts[2], parts[3], parts[4]);
        } else {
            throw new IllegalArgumentException("Invalid task type or field count");
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Converts a validated task to the pipe-separated format used by the loader. */
    private static String serializeTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return String.format("D | %s | %s | %s", status, task.getDescription(), deadline.getDeadline());
        }
        if (task instanceof Event event) {
            return String.format("E | %s | %s | %s | %s", status, task.getDescription(),
                    event.getFrom(), event.getTo());
        }
        return String.format("T | %s | %s", status, task.getDescription());
    }
}
