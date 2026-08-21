import java.util.Scanner;

enum Command {
    LIST, ADD, BYE, MARK, UNMARK, TODO, EVENT, DEADLINE
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
            String input = scanner.nextLine();
            Command command = this.getCommand(input);
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
                    this.taskList.addToDo(input);
                    break;
            }
        }
    }

    // helper methods
    private Command getCommand(String input) {
        String fInput = input.toLowerCase();
        if (fInput.equals("bye")) {
            return Command.BYE;
        }
        if (fInput.equals("list")) {
            return Command.LIST;
        }
        if (fInput.startsWith("mark ")) {
            return Command.MARK;
        }
        if (fInput.startsWith("unmark ")) {
            return Command.UNMARK;
        }
        if (fInput.startsWith("deadline ")) {
            return Command.DEADLINE;
        }
        if (fInput.startsWith("event ")) {
            return Command.EVENT;
        }
        if (fInput.startsWith("todo ")) {
            return Command.TODO;
        }
        return Command.ADD;
    }

    private void addDeadline(String input) {
        String[] words = input.split("deadline|/by");
        String description = words[1].trim();
        String deadline = words[2].trim();
        this.taskList.addDeadline(description, deadline);
    }

    private void addEvent(String input) {
        String[] words = input.split("event|/from|/to");
        String description = words[1].trim();
        String from = words[2].trim();
        String to = words[3].trim();
        this.taskList.addEvent(description, from, to);
    }

    private void addToDo(String input) {
        String description = input.split("\\s+", 2)[1].trim();
        this.taskList.addToDo(description);
    }

    private void markAsDone(String input) {
        int index = this.getTaskIndex(input);
        if (index == -1) {
            System.out.println("Please enter a valid task number.");
            return;
        }
        this.taskList.markAsDone(index);
    }

    private void markAsUndone(String input) {
        int idx = getTaskIndex(input);
        if (idx == -1) {
            System.out.println("Please enter a valid task number.");
        }
        this.taskList.markAsUndone(idx);
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
