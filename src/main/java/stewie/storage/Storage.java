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
        if (isReadOnly) {
            throw new StorageException("Tasks are read-only. Back up and repair the task file, then restart.");
        }
        Path temporaryFile = null;
        try {
            if (Files.isSymbolicLink(storagePath) || !Arrays.equals(savedContent, readExistingFile())) {
                throw new StorageException("The task file changed outside Stewie. Restart before making changes.");
            }
            if (Files.exists(storagePath, LinkOption.NOFOLLOW_LINKS) && !Files.isWritable(storagePath)) {
                throw new IOException("Task file is not writable");
            }
            Files.createDirectories(storagePath.getParent());
            String content = tasks.stream().map(Storage::serializeTask).collect(Collectors.joining("\n"));
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            temporaryFile = Files.createTempFile(storagePath.getParent(), ".stewie-", ".tmp");
            Files.write(temporaryFile, bytes);
            if (Files.exists(storagePath) && Files.getFileStore(storagePath).supportsFileAttributeView("posix")) {
                Files.setPosixFilePermissions(temporaryFile, Files.getPosixFilePermissions(storagePath));
            }
            Files.move(temporaryFile, storagePath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            savedContent = bytes;
        } catch (IOException | SecurityException exception) {
            throw new StorageException("Unable to save tasks. No changes were applied. "
                    + "Check the task file, permissions, free space, and support for atomic file replacement.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException exception) {
                    // A leftover temporary file is safer than touching the original data.
                }
            }
        }
    }

    /**
     * Loads valid records and protects the original file if any record cannot be trusted.
     *
     * @return Valid tasks in their original order, including records after a malformed line.
     */
    public ArrayList<Task> loadFromDisk() {
        ArrayList<Task> tasks = new ArrayList<>();
        isReadOnly = false;
        loadWarning = "";
        try {
            if (Files.isSymbolicLink(storagePath)) {
                throw new IOException("Symbolic links are not supported for task files");
            }
            savedContent = readExistingFile();
            if (savedContent == null) {
                return tasks;
            }
            String content = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(savedContent)).toString();
            String[] lines = content.split("\\R", -1);
            ArrayList<String> invalidLines = new ArrayList<>();
            for (int index = 0; index < lines.length; index++) {
                if (index == lines.length - 1 && lines[index].isEmpty()) {
                    continue;
                }
                try {
                    Task task = parseTask(lines[index]);
                    if (tasks.stream().anyMatch(task::hasSameDetails)) {
                        throw new IllegalArgumentException("Duplicate task");
                    }
                    tasks.add(task);
                } catch (IllegalArgumentException exception) {
                    invalidLines.add(Integer.toString(index + 1));
                }
            }
            if (!invalidLines.isEmpty()) {
                isReadOnly = true;
                loadWarning = "Storage warning: Invalid or duplicate records at lines "
                        + String.join(", ", invalidLines) + ". Valid tasks are available read-only. "
                        + "Back up and repair the task file, then restart.";
            }
        } catch (IOException | SecurityException exception) {
            isReadOnly = true;
            loadWarning = "Storage warning: Unable to read the task file. Tasks are read-only. "
                    + "Check the file path, permissions, and UTF-8 content, then restart.";
        }
        return tasks;
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
