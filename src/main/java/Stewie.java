import java.util.Scanner;

public class Stewie {
    private String[] tasks;
    private int taskCount;

    public Stewie() {
        this.tasks = new String[100];
        this.taskCount = 0;
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

    public void addList(String byeMsg) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Tell me whats on your list!!");
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println(byeMsg);
                break;
            }
            if (input.equals("list")) {
                if (this.taskCount == 0) {
                    System.out.println("You have no task saved.");
                } else {
                    for (int i = 0; i < this.taskCount; i++) {
                        System.out.printf("%d : %s %n", i + 1, this.tasks[i]);
                    }
                }
            } else {
                this.tasks[this.taskCount] = input;
                this.taskCount++;
            }
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
        String greeting = "Hey there! I'm Stewie. \nWanna have a chat?";
        String byeMsg = "Bye! See you later.";
        System.out.println(banner);
        System.out.println(greeting);
        //this.echoUserCommands(byeMsg);
        this.addList(byeMsg);
    }

    public static void main(String[] args) {
        new Stewie().run();
    }
}
