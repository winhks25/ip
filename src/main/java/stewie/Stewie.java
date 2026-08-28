package stewie;

import java.util.Scanner;

/**
 * Represent the chatbot Stewie.
 * Stewie has task list to stores the tasks users want to record.
 * Stewie parses the text inputs and stores them as Task in task list.
 */
public class Stewie {
    private final TaskList taskList;

    /**
     * Initialize a chatbot Stewie with a task list from local disk.
     */
    public Stewie() {
        this.taskList = new TaskList();
    }

    /**
     * Runs the Chatbot.
     * Prints the banner STEWIE and prompt the user to type in commands.
     * Responds to the user based on their commands.
     */
    public void run() {
        Ui.greetUser();
        Scanner scanner = new Scanner(System.in);

        // Conversation starts here
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().toLowerCase().trim();
            Command command = Parser.getCommand(input);
            try {
                switch (command) {
                    case BYE:
                        Ui.printBye();
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
                    case DELETE:
                        this.deleteTask(input);
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
    /**
     * Adds a task of type deadline to the task list.
     * Records the description and deadline date in task list.
     *
     * @param input Input from user.
     */
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

    /**
     * Adds a task of type event to the task list.
     * Records description, starting time and ending time of the event.
     *
     * @param input Input from user.
     */
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

    /**
     * Adds a task of type ToDo to the task list.
     * Records description of the task.
     *
     * @param input Input from user.
     */
    private void addToDo(String input) {
        try {
            String description = input.split("\\s+", 2)[1].trim();
            this.taskList.addToDo(description);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please add a task description.");
        }
    }

    /**
     * Marks the task as done.
     *
     * @param input Input from user.
     */
    private void markAsDone(String input) {
        int index = Parser.getTaskIndex(input);
        if (index == -1) {
            System.out.println("Please enter a valid task number in the format: mark <number>.");
            return;
        } else {
            this.taskList.markAsDone(index);
        }
    }

    /**
     * Marks the task as not done.
     *
     * @param input Input from user.
     */
    private void markAsUndone(String input) {
        int idx = Parser.getTaskIndex(input);
        if (idx == -1) {
            System.out.println("Please enter a valid task number in the format: unmark <number>.");
        } else {
            this.taskList.markAsUndone(idx);
        }
    }

    /**
     * Delete the task from the task list.
     *
     * @param input Input from user.
     */
    private void deleteTask(String input) {
        int idx = Parser.getTaskIndex(input);
        if (idx == -1) {
            System.out.println("Please enter a valid task number in the format: delete <number>.");
        } else {
            this.taskList.deleteTask(idx);
        }
    }

    public static void main(String[] args) {
        new Stewie().run();
    }
}
