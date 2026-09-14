package stewie;

import java.util.Scanner;

import stewie.model.TaskList;
import stewie.parser.Command;
import stewie.parser.Parser;
import stewie.storage.StorageException;
import stewie.ui.cli.Ui;

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
        if (!taskList.getLoadWarning().isEmpty()) {
            System.out.println(taskList.getLoadWarning());
        }
        Scanner scanner = new Scanner(System.in);

        // Conversation starts here
        while (scanner.hasNextLine()) {
            String input = Parser.normalize(scanner.nextLine());
            Command command = Parser.getCommand(input);
            assert command != null : "Parser must classify every input command";
            try {
                switch (command) {
                    case BYE:
                        Ui.printBye();
                        return;
                    case LIST:
                        Ui.printTaskList(this.taskList.produceTaskList());
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
                    case FIND:
                        this.findTasks(input);
                        break;
                    case UPDATE:
                        this.updateTask(input);
                        break;
                    default:
                        System.out.println("What precisely is the plan? Use a command: todo, event, deadline, "
                                + "mark, unmark, delete, find, "
                                + "update, list, bye "
                                + "+ description!");
                        break;
                }
            } catch (StorageException exception) {
                System.out.println(exception.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("A slight flaw in your plan: " + e.getMessage());
            }
        }
    }

    // helper methods
    /**
     * Adds a task of type deadline to the task list.
     * Deadline task includes description and deadline date.
     *
     * @param input Input from user.
     */
    private void addDeadline(String input) {
        String[] parsedInput = Parser.parseDeadline(input);
        this.taskList.addDeadline(parsedInput[0], parsedInput[1]);
    }

    /**
     * Adds a task of type event to the task list.
     * An event has a description, starting time and ending time.
     *
     * @param input Input from user.
     */
    private void addEvent(String input) {
        String[] parsedInput = Parser.parseEvent(input);
        this.taskList.addEvent(parsedInput[0], parsedInput[1], parsedInput[2]);
    }

    /**
     * Adds a task of type ToDo to the task list.
     * Records description of the task.
     *
     * @param input Input from user.
     */
    private void addToDo(String input) {
        this.taskList.addToDo(Parser.parseTodo(input));
    }

    /**
     * Marks the task as done.
     *
     * @param input Input from user.
     */
    private void markAsDone(String input) {
        int index = Parser.getTaskIndex(input);
        if (index == -1) {
            System.out.println("Numbers, please. Use: mark <number>.");
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
            System.out.println("Numbers, please. Use: unmark <number>.");
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
            System.out.println("Numbers, please. Use: delete <number>.");
        } else {
            this.taskList.deleteTask(idx);
        }
    }

    /**
     * Updates a task description.
     *
     * @param input Input from user.
     */
    private void updateTask(String input) {
        int index = Parser.getUpdateTaskIndex(input);
        String[] updates = Parser.parseUpdate(input);
        if (index == -1 || areAllUpdateFieldsMissing(updates)) {
            System.out.println("A revision needs details. Use: update <number> [description] "
                    + "[d/<deadline>] [from/<from>] [to/<to>]");
            return;
        }
        this.taskList.updateTask(index, updates[0], updates[1], updates[2], updates[3]);
    }

    /**
     * Checks whether an update command contains at least one field.
     *
     * @param updates Parsed update fields.
     * @return True when no update field was supplied.
     */
    private boolean areAllUpdateFieldsMissing(String[] updates) {
        for (String update : updates) {
            if (update != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prints the tasks that contains the keywords from input.
     *
     * @param input Input from user.
     */
    private void findTasks(String input) {
        String[] keywords = Parser.parseFindKeywords(input);
        Ui.printTaskList(this.taskList.findTasks(keywords));
    }

    /**
     * Starts the console task assistant.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Stewie().run();
    }
}
