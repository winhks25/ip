import java.util.Scanner;

enum Command {
    LIST, ERROR, BYE, MARK, UNMARK, TODO, EVENT, DEADLINE
}

public class Stewie {
    private TaskList taskList;

    public Stewie() {
        this.taskList = new TaskList();
    }

    public void echoUserCommands(String byeMsg) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println(byeMsg);
                break;
            }
            System.out.println("Stewie: " + input);
        }
    }

    public void run() {
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
        Scanner scanner = new Scanner(System.in);

        // Conversation starts here
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().toLowerCase().trim();
            Command command = this.getCommand(input);
            try {
                switch (command) {
                    case BYE:
                        this.printBye();
                        return;
                    case LIST:
                        this.taskList.printTaskList();
                        break;
                    case MARK:
                        this.markAsDone(input);
                        break;
                    case UNMARK:
                        this.markAsUndone(input);
                        break;
                    case DEADLINE:
                        this.addDeadline(input);
                        break;
                    case EVENT:
                        this.addEvent(input);
                        break;
                    case TODO:
                        this.addToDo(input);
                        break;
                    default:
                        System.out.println("Please add a command: todo, event, deadline, mark, unmark, list, bye + description!");
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // helper methods
    private Command getCommand(String input) {
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
        return Command.ERROR;
    }

    private void addDeadline(String input) {
        try {
            String[] words = input.split("deadline|/by");
            String description = words[1].trim();
            String deadline = words[2].trim();
            this.taskList.addDeadline(description, deadline);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Add deadline task in the format: deadline <description> /by <deadline>");
        }
    }

    private void addEvent(String input) {
        try {
            String[] words = input.split("event|/from|/to");
            String description = words[1].trim();
            String from = words[2].trim();
            String to = words[3].trim();
            this.taskList.addEvent(description, from, to);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Add event tasks in the format: event <description> /from <date or time> /to<date or time>");
        }
    }

    private void addToDo(String input) {
        try {
            String description = input.split("\\s+", 2)[1].trim();
            this.taskList.addToDo(description);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please add a task description.");
        }
    }

    private void markAsDone(String input) {
        int index = this.getTaskIndex(input);
        if (index == -1) {
            System.out.println("Please enter a valid task number.");
            return;
        } else {
            this.taskList.markAsDone(index);
        }
    }

    private void markAsUndone(String input) {
        int idx = getTaskIndex(input);
        if (idx == -1) {
            System.out.println("Please enter a valid task number.");
        } else {
            this.taskList.markAsUndone(idx);
        }
    }

    private void printBye() {
        System.out.println("Bye, see you later!");
    }

    private int getTaskIndex(String input) {
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

    public static void main(String[] args) {
        new Stewie().run();
    }
}
