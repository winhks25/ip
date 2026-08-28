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
}

