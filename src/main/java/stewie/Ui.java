package stewie;

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
        System.out.println("Bye, see you later!");
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
        System.out.println("Hey there! I'm Stewie. \nWanna have a chat?");
        System.out.println("Tell me whats on your list!!");
    }

    /**
     * Print the success message upon the successful addition of a task to the list.
     *
     * @param t Task being added.
     * @param numTasks Number of tasks in the task list.
     */
    public static void printTaskAddConfirmation(Task t, int numTasks) {
        System.out.println("Got it! Added the following to your list.");
        System.out.println(t);
        System.out.printf("Now you have %d tasks in the list. %n", numTasks);
    }

    /**
     * Print the format for mark and unmark commands
     * @param command Type of command: mark or unmark or delete
     */
    public static void printNumberedCommandFormat(String command) {
        System.out.printf("Please type in a valid task number in the format: %s <number> %n", command);
    }

    /**
     * Print all the tasks in the task list
     */
    public static void printTaskList(String[] tasks) {
        System.out.println("Here is your list of tasks.");
        if (tasks.length == 0) {
            System.out.println("You have no task saved.");
            return;
        }

        for (int i = 0; i < tasks.length; i++) {
            System.out.println(i + 1 + ". " + tasks[i]);
        }
    }
}

