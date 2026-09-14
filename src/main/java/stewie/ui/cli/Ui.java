package stewie.ui.cli;

import stewie.model.Task;
import stewie.ui.Dialogue;

import java.util.Scanner;

/**
 * Contains the UI components of the program.
 */
public class Ui {
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
