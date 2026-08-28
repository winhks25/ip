package stewie;

import java.util.ArrayList;

/**
 * Represents a task list.
 * Task objects are stored in an arrayList.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Initialize a task list with data from the disk.
     */
    public TaskList() {
        this.tasks = Storage.loadFromDisk();
    }

    /**
     * Adds a task of type todo to the task list.
     * Saves the task to the disk.
     *
     * @param description Description of the task.
     */
    public void addToDo(String description) {
        Task newTask = new ToDo(description);
        this.tasks.add(newTask);
        Storage.saveToDisk(this.tasks);
        Ui.printTaskAddConfirmation(newTask, this.tasks.size());
    }

    /**
     * Adds a task of type event to the task list.
     * Saves the event to the disk.
     *
     * @param description Description of the event.
     * @param from Start time of the event.
     * @param to End time of the event.
     */
    public void addEvent(String description, String from, String to) {
        Task newTask = new Event(description, from, to);
        this.tasks.add(newTask);
        Storage.saveToDisk(this.tasks);
        Ui.printTaskAddConfirmation(newTask, this.tasks.size());
    }

    /**
     * Adds a task of type deadline to the task list.
     * Saves the task with deadline to the disk.
     *
     * @param description Description of the deadline task.
     * @param deadline Deadline date of the task.
     */
    public void addDeadline(String description, String deadline) {
        Task newTask = new Deadline(description, deadline);
        this.tasks.add(newTask);
        Storage.saveToDisk(this.tasks);
        Ui.printTaskAddConfirmation(newTask, this.tasks.size());
    }

    /**
     * Marks a task as done.
     * Saves the changes to the disk.
     *
     * @param index Index of the task to be marked as done.
     */
    public void markAsDone(int index) {
        try {
            this.tasks.get(index).markAsDone();
            Storage.saveToDisk(this.tasks);
        } catch (IndexOutOfBoundsException e) {
            Ui.printNumberedCommandFormat("mark");
        }
    }

    /**
     * Marks a task as not done.
     * Saves the changes to the disk.
     *
     * @param index Index of the task to be marked as not done.
     */
    public void markAsUndone(int index) {
        try {
            this.tasks.get(index).markAsUndone();
            Storage.saveToDisk(this.tasks);
        } catch (IndexOutOfBoundsException e) {
            Ui.printNumberedCommandFormat("unmark");
        }
    }

    /**
     * Delete a task from the task list.
     * Saves the changes to the disk.
     *
     * @param index Index of the task to be deleted.
     */
    public void deleteTask(int index) {
        try {
            this.tasks.remove(index);
            Storage.saveToDisk(this.tasks);
        } catch (IndexOutOfBoundsException e) {
            Ui.printNumberedCommandFormat("delete");
        }
    }

    /**
     * Returns the tasks in string array.
     *
     * @return Tasks as a string array.
     */
    public String[] produceTaskList() {
        String[] lst = new String[this.tasks.size()];

        for (int i = 0; i < this.tasks.size(); i++) {
            lst[i] = this.tasks.get(i).toString();
        }
        return lst;
    }
}
