package stewie.ui.cli;

import java.util.Scanner;

import stewie.model.Task;
import stewie.ui.Dialogue;

/**
 * Contains the UI components of the program.
 */
public class Ui {
    /**
     * Prints a nonempty warning from loading saved tasks.
     *
     * @param warning Warning text, or an empty string after a clean load.
     */
    public static void printLoadWarning(String warning) {
        if (!warning.isEmpty()) {
            System.out.println(warning);
        }
    }

    /**
     * Prints a storage failure without confirming a task change.
     *
     * @param message Explanation of the storage failure.
     */
    public static void printStorageError(String message) {
        System.out.println(message);
    }

    /**
     * Prints guidance for invalid user input.
     *
     * @param message Explanation of the invalid input.
     */
    public static void printInputError(String message) {
        System.out.println("A slight flaw in your plan: " + message);
    }

    /** Prints the supported commands when input cannot be classified. */
    public static void printUnknownCommand() {
        System.out.println("What precisely is the plan? Use a command: todo, event, deadline, "
                + "mark, unmark, delete, find, update, list, bye + description!");
    }

    /**
     * Prints guidance for a missing or malformed task number.
     *
     * @param command Command requiring a task number.
     */
    public static void printMissingTaskNumber(String command) {
        System.out.println("Numbers, please. Use: " + command + " <number>.");
    }

    /** Prints the format for an update missing its task number or replacement fields. */
    public static void printUpdateFormat() {
        System.out.println("A revision needs details. Use: update <number> [description] "
                + "[d/<deadline>] [from/<from>] [to/<to>]");
    }

    /**
     * Prints whatever users type in.
     */
    public static void echoUserCommands() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                Ui.printBye();
                break;
            }
            System.out.println("Stewie: " + input);
        }
    }

    /**
     * Print goodbye statement.
     */
    public static void printBye() {
        System.out.println(Dialogue.GOODBYE);
    }

    /**
     * Prints the banner and greeting messages to the user.
     */
    public static void greetUser() {
        String banner = """
                ███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
                ██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
                ███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
                ╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
                ███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
                ╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝
                """;
        System.out.println(banner);
        System.out.println(Dialogue.GREETING);
    }

    /**
     * Print the success message upon the successful addition of a task to the list.
     *
     * @param t Task being added.
     * @param numTasks Number of tasks in the task list.
     */
    public static void printTaskAddConfirmation(Task t, int numTasks) {
        System.out.println(Dialogue.ADDED);
        System.out.println(t);
        System.out.printf("Your agenda now contains %d %s. Do try to keep up.%n",
                numTasks, numTasks == 1 ? "task" : "tasks");
    }

    /**
     * Prints the success message after a task is updated.
     *
     * @param task Updated task.
     */
    public static void printTaskUpdateConfirmation(Task task) {
        System.out.println(Dialogue.UPDATED);
        System.out.println(task);
    }

    /**
     * Print the format for mark and unmark commands
     * @param command Type of command: mark or unmark or delete
     */
    public static void printNumberedCommandFormat(String command) {
        System.out.printf("That task exists only in your imagination. Use a listed number: %s <number>%n", command);
    }

    /**
     * Prints all tasks in the task list.
     *
     * @param tasks Formatted tasks to display.
     */
    public static void printTaskList(String[] tasks) {
        System.out.println(Dialogue.LIST);
        if (tasks.length == 0) {
            System.out.println(Dialogue.EMPTY);
            return;
        }

        for (int i = 0; i < tasks.length; i++) {
            System.out.println(i + 1 + ". " + tasks[i]);
        }
    }
}
