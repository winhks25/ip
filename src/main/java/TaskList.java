import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public void addToDo(String description) {
        Task newTask = new ToDo(description);
        this.tasks.add(newTask);

        System.out.println("Got it! Added the following to your list.");
        System.out.println(newTask);
        System.out.printf("Now you have %d tasks in the list. %n", this.tasks.size());
    }

    public void addEvent(String description, String from, String to) {
        Task newTask = new Event(description, from, to);
        this.tasks.add(newTask);

        System.out.println("Got it! Added the following to your list.");
        System.out.println(newTask);
        System.out.printf("Now you have %d tasks in the list. %n", this.tasks.size());
    }

    public void addDeadline(String description, String deadline) {
        Task newTask = new Deadline(description, deadline);
        this.tasks.add(newTask);

        System.out.println("Got it! Added the following to your list.");
        System.out.println(newTask);
        System.out.printf("Now you have %d tasks in the list. %n", this.tasks.size());
    }

    public void markAsDone(int index) {
        try {
            this.tasks.get(index).markAsDone();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Please type in a valid task number in the format: mark <number>");
        }
    }

    public void markAsUndone(int index) {
        try {
            this.tasks.get(index).markAsUndone();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Please type in a valid task number in the format: mark <number>");
        }
    }

    public void deleteTask(int index) {
        try {
            this.tasks.remove(index);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Please enter a valid task number in the format: delete <number>");
        }
    }

    public void printTaskList() {
        System.out.println("Here is your list of tasks.");
        if (this.tasks.isEmpty()) {
            System.out.println("You have no task saved.");
            return;
        }

        for (int i = 0; i < this.tasks.size(); i++) {
            System.out.println(i + 1 + ". " + this.tasks.get(i).toString());
        }
    }
}
