package stewie;

/**
 * Contains methods to parse user input into commands that the chatbot understands
 */
public class Parser {
    /**
     * Returns a type of command from user's input.
     *
     * @param input Input text string the user typed in.
     * @return command A type of valid command.
     */
    public static Command getCommand(String input) {
        if (input.equals("bye")) {
            return Command.BYE;
        }
        if (input.equals("list")) {
            return Command.LIST;
        }
        if (input.startsWith("mark ")) {
            return Command.MARK;
        }
        if (input.startsWith("unmark ")) {
            return Command.UNMARK;
        }
        if (input.startsWith("deadline ")) {
            return Command.DEADLINE;
        }
        if (input.startsWith("event ")) {
            return Command.EVENT;
        }
        if (input.startsWith("todo ")) {
            return Command.TODO;
        }
        if (input.startsWith("delete ")) {
            return Command.DELETE;
        }
        return Command.ERROR;
    }

    /**
     * Returns the index of the task from the user input string.
     *
     * @param input Input from user.
     * @return Index of the task from the input string.
     */
    public static int getTaskIndex(String input) {
        String[] parts = input.trim().split("\\s+");

        if (parts.length != 2) {
            return -1;
        }

        try {
            return Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
