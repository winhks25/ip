package stewie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Contains methods that stores and retrieve the task from local disk.
 */
public class Storage {
    private static final Path STORAGE_PATH = Path.of("./data/stewie.txt");

    /**
     * Saves tasks in the current task list to the disk.
     */
    public static void saveToDisk(ArrayList<Task> tasks) {
        try {
            Files.createDirectories(Path.of("./data"));

            String content = tasks.stream()
                    .map(Storage::serializeTask)
                    .collect(Collectors.joining(System.lineSeparator()));

            Files.writeString(STORAGE_PATH, content);
        } catch (IOException e) {
            System.out.println("Unable to save tasks.");
        }
    }

    /**
     * Loads the tasks from the local disk to populate the task list of the object.
     */
    public static ArrayList<Task> loadFromDisk() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(STORAGE_PATH)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(STORAGE_PATH)) {
                String[] parts = line.split("\\s*\\|\\s*", -1);
                if (parts.length < 3) {
                    continue;
                }

                String type = parts[0];
                boolean done = parts[1].equals("1");
                Task task;
                if (type.equals("T") && parts.length == 3) {
                    task = new ToDo(parts[2]);
                } else if (type.equals("D") && parts.length == 4) {
                    task = new Deadline(parts[2], parts[3]);
                } else if (type.equals("E") && parts.length == 5) {
                    task = new Event(parts[2], parts[3], parts[4]);
                } else {
                    continue;
                }

                if (done) {
                    task.markAsDone();
                }
                tasks.add(task);
            }
        } catch (IOException e) {
            System.out.println("Unable to load tasks.");
        }
        return tasks;
    }

    /**
     * Converts a task to the pipe-separated format used by the loader.
     */
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
