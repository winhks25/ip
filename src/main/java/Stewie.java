import java.util.Scanner;

public class Stewie {
    public static void main(String[] args) {
        String banner = """
                ███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
                ██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
                ███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
                ╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
                ███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
                ╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝
                """;
        String greeting = "Hey there! I'm Stewie. \nWanna have a chat?";
        String bye = "Bye! See you later.";
        System.out.println(banner);
        System.out.println(greeting);
        echoUserCommands(bye);
    }

    public static void echoUserCommands(String byeMsg) {
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
}
