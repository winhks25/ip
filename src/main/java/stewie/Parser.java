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

    /**
     * Parses a string into description and deadline.
     * Returns description and deadline as a string array.
     *
     * @param input Input string from user.
     * @return {description, deadline} in String[] format.
     */
    public static String[] parseDeadline(String input) {
        String[] words = input.split("deadline|/by");
        String description = words[1].trim();
        String deadline = words[2].trim();
        return new String[] {description, deadline};
    }

    /**
     * Parses a string into description, from(date), and to(date).
     * Returns them as a string array.
     *
     * @param input Input from user.
     * @return {description, from, to} string array.
     */
    public static String[] parseEvent(String input) {
        String[] words = input.split("event|/from|/to");
        String description = words[1].trim();
        String from = words[2].trim();
        String to = words[3].trim();
        return new String[] {description, from, to};
    }

    /**
     * Parses a string into description.
     *
     * @param input Input from user.
     * @return description extracted from user.
     */
    public static String parseTodo(String input) {
        return input.split("\\s+", 2)[1].trim();
    }
}
