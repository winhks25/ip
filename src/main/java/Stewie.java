import java.util.Scanner;

enum Command {
    LIST, BYE, ADD, MARK, UNMARK
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
                    int index = getTaskIndex(input);
                    if (index == -1) {
                        System.out.println("Please enter a valid task number.");
                        break;
                    }
                    this.taskList.markAsDone(index);
                    break;
                case UNMARK:
                    int idx = getTaskIndex(input);
                    if (idx == -1) {
                        System.out.println("Please enter a valid task number.");
                        break;
                    }
                    this.taskList.markAsUndone(idx);
                    break;
                default:
                    this.taskList.addTask(input);
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
        return Command.ADD;
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
