import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/** Stores the user's tasks and saves them after every successful change. */
public class TaskList {
    private static final Path STORAGE_PATH = Path.of("./data/stewie.txt");
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
        loadFromDisk();
    }

    public void addToDo(String description) {
        Task newTask = new ToDo(description);
        this.tasks.add(newTask);
        saveToDisk();

        System.out.println("Got it! Added the following to your list.");
        System.out.println(newTask);
        System.out.printf("Now you have %d tasks in the list. %n", this.tasks.size());
    }

    public void addEvent(String description, String from, String to) {
        Task newTask = new Event(description, from, to);
        this.tasks.add(newTask);
        saveToDisk();

        System.out.println("Got it! Added the following to your list.");
        System.out.println(newTask);
        System.out.printf("Now you have %d tasks in the list. %n", this.tasks.size());
    }

    public void addDeadline(String description, String deadline) {
        Task newTask = new Deadline(description, deadline);
        this.tasks.add(newTask);
        saveToDisk();

        System.out.println("Got it! Added the following to your list.");
        System.out.println(newTask);
        System.out.printf("Now you have %d tasks in the list. %n", this.tasks.size());
    }

    public void markAsDone(int index) {
        try {
            this.tasks.get(index).markAsDone();
            saveToDisk();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Please type in a valid task number in the format: mark <number>");
        }
    }

    public void markAsUndone(int index) {
        try {
            this.tasks.get(index).markAsUndone();
            saveToDisk();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Please type in a valid task number in the format: mark <number>");
        }
    }

    public void deleteTask(int index) {
        try {
            this.tasks.remove(index);
            saveToDisk();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Please enter a valid task number in the format: delete <number>");
        }
    }

    public void printTaskList() {
        System.out.println("Here is your list of tasks.");
        if (this.tasks.isEmpty()) {
            System.out.println("You have no task saved.");
            return;
        }

        for (int i = 0; i < this.tasks.size(); i++) {
            System.out.println(i + 1 + ". " + this.tasks.get(i).toString());
        }
    }

    private void saveToDisk() {
        try {
            Files.createDirectories(Path.of("./data"));

            String content = tasks.stream()
                    .map(this::serializeTask)
                    .collect(Collectors.joining(System.lineSeparator()));

            Files.writeString(STORAGE_PATH, content);
        } catch (IOException e) {
            System.out.println("Unable to save tasks.");
        }
    }

    /** Loads previously saved tasks without changing the file while starting up. */
    private void loadFromDisk() {
        if (!Files.exists(STORAGE_PATH)) {
            return;
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
                this.tasks.add(task);
            }
        } catch (IOException e) {
            System.out.println("Unable to load tasks.");
        }
    }

    /** Converts a task to the pipe-separated format used by the loader. */
    private String serializeTask(Task task) {
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
