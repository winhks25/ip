package stewie.parser;

import java.util.Arrays;

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
        assert input != null : "Command parsing requires normalized input";
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
        if (input.startsWith("find ")) {
            return Command.FIND;
        }
        if (input.startsWith("update ")) {
            return Command.UPDATE;
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
        assert input != null : "Task index parsing requires command input";
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

    /**
     * Returns the task index from an update command.
     *
     * @param input Input from user.
     * @return Zero-based task index, or -1 when the command has no valid index.
     */
    public static int getUpdateTaskIndex(String input) {
        assert input != null : "Update parsing requires command input";
        String[] parts = input.trim().split("\\s+");

        if (parts.length < 3) {
            return -1;
        }

        try {
            return Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Returns the replacement description from an update command.
     *
     * @param input Input from user.
     * @return Replacement task description, or an empty string when it is missing.
     */
    public static String parseUpdateDescription(String input) {
        assert input != null : "Update parsing requires command input";
        String[] updates = parseUpdate(input);
        return updates[0] == null ? "" : updates[0];
    }

    /**
     * Parses the optional fields in an update command.
     * The returned values are description, deadline, from, and to, respectively.
     * A null value means that the corresponding task field should be preserved.
     *
     * @param input Input from user.
     * @return Updated task fields in the order description, deadline, from, and to.
     */
    public static String[] parseUpdate(String input) {
        assert input != null : "Update parsing requires command input";
        String[] parts = input.trim().split("\\s+", 3);
        if (parts.length < 3) {
            return new String[] {null, null, null, null};
        }

        String body = parts[2].trim();
        String description = body;
        String deadline = null;
        String from = null;
        String to = null;
        String[] markers = {" d/", " by/", " from/", " to/"};
        int firstMarker = body.length();
        for (String marker : markers) {
            int markerIndex = body.indexOf(marker);
            if (markerIndex >= 0 && markerIndex < firstMarker) {
                firstMarker = markerIndex;
            }
        }
        description = body.substring(0, firstMarker).trim();

        String fields = body.substring(firstMarker).trim();
        while (!fields.isEmpty()) {
            int slashIndex = fields.indexOf('/');
            if (slashIndex <= 0) {
                return new String[] {null, null, null, null};
            }
            String marker = fields.substring(0, slashIndex).trim();
            int nextMarker = findNextUpdateMarker(fields, slashIndex + 1);
            String value = fields.substring(slashIndex + 1, nextMarker).trim();
            if (value.isEmpty()) {
                return new String[] {null, null, null, null};
            }
            switch (marker) {
                case "d":
                case "by":
                    deadline = value;
                    break;
                case "from":
                    from = value;
                    break;
                case "to":
                    to = value;
                    break;
                default:
                    return new String[] {null, null, null, null};
            }
            fields = nextMarker == fields.length() ? "" : fields.substring(nextMarker).trim();
        }
        return new String[] {description.isEmpty() ? null : description, deadline, from, to};
    }

    /**
     * Finds the next supported update field marker.
     *
     * @param fields Update fields to search.
     * @param startIndex Position at which to start searching.
     * @return Index of the next marker, or the end of the input.
     */
    private static int findNextUpdateMarker(String fields, int startIndex) {
        int nextMarker = fields.length();
        String[] markers = {" d/", " by/", " from/", " to/"};
        for (String marker : markers) {
            int markerIndex = fields.indexOf(marker, startIndex - 1);
            if (markerIndex >= 0 && markerIndex < nextMarker) {
                nextMarker = markerIndex;
            }
        }
        return nextMarker;
    }

    /**
     * Parses a string to keywords string array.
     *
     * @param input Input string from user.
     * @return Words starting from second word in input.
     */
    public static String[] parseFindKeywords(String input) {
        String[] words = input.split("\\s+");
        return Arrays.copyOfRange(words, 1, words.length);
    }
}
