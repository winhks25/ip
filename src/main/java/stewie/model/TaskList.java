package stewie.model;

import stewie.storage.Storage;
import stewie.ui.cli.Ui;

import java.util.ArrayList;
import java.util.Arrays;

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
        assert this.tasks != null : "Storage must return a task list";
    }

    /**
     * Adds a task of type todo to the task list.
     * Saves the task to the disk.
     *
     * @param description Description of the task.
     */
    public void addToDo(String description) {
        addTask(new ToDo(description));
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
        addTask(new Event(description, from, to));
    }

    /**
     * Adds a task of type deadline to the task list.
     * Saves the task with deadline to the disk.
     *
     * @param description Description of the deadline task.
     * @param deadline Deadline date of the task.
     */
    public void addDeadline(String description, String deadline) {
        addTask(new Deadline(description, deadline));
    }

    /**
     * Adds a task, persists the updated list, and confirms the addition.
     *
     * @param task Task to add.
     */
    private void addTask(Task task) {
        this.tasks.add(task);
        Storage.saveToDisk(this.tasks);
        Ui.printTaskAddConfirmation(task, this.tasks.size());
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
     * Updates a task description while preserving its type, metadata, and completion status.
     *
     * @param index Index of the task to update.
     * @param description Replacement description.
     */
    public void updateTask(int index, String description) {
        updateTask(index, description, null, null, null);
    }

    /**
     * Updates selected fields of a task while preserving all omitted fields.
     *
     * @param index Index of the task to update.
     * @param description Replacement description, or null to preserve it.
     * @param deadlineValue Replacement deadline, or null to preserve it.
     * @param from Replacement event start time, or null to preserve it.
     * @param to Replacement event end time, or null to preserve it.
     */
    public void updateTask(int index, String description, String deadlineValue, String from, String to) {
        try {
            Task existingTask = this.tasks.get(index);
            Task updatedTask;
            if (existingTask instanceof Deadline deadline) {
                if (from != null || to != null) {
                    throw new IllegalArgumentException("Deadline tasks only support descriptions and deadlines.");
                }
                String updatedDescription = description == null ? existingTask.getDescription() : description;
                String updatedDeadline = deadlineValue == null ? deadline.getDeadline() : deadlineValue;
                updatedTask = new Deadline(updatedDescription, updatedDeadline);
            } else if (existingTask instanceof Event event) {
                if (deadlineValue != null) {
                    throw new IllegalArgumentException("Event tasks only support descriptions and event times.");
                }
                String updatedDescription = description == null ? existingTask.getDescription() : description;
                String updatedFrom = from == null ? event.getFrom() : from;
                String updatedTo = to == null ? event.getTo() : to;
                updatedTask = new Event(updatedDescription, updatedFrom, updatedTo);
            } else {
                if (deadlineValue != null || from != null || to != null) {
                    throw new IllegalArgumentException("Todo tasks only support descriptions.");
                }
                String updatedDescription = description == null ? existingTask.getDescription() : description;
                updatedTask = new ToDo(updatedDescription);
            }

            if (existingTask.isDone()) {
                updatedTask.markAsDone();
            }
            this.tasks.set(index, updatedTask);
            Storage.saveToDisk(this.tasks);
            Ui.printTaskUpdateConfirmation(updatedTask);
        } catch (IndexOutOfBoundsException e) {
            Ui.printNumberedCommandFormat("update");
        }
    }

    /**
     * Returns the tasks in string array.
     *
     * @return Tasks as a string array.
     */
    public String[] produceTaskList() {
        String[] taskDescriptions = new String[this.tasks.size()];

        for (int i = 0; i < this.tasks.size(); i++) {
            assert this.tasks.get(i) != null : "A task list must not contain null tasks";
            taskDescriptions[i] = this.tasks.get(i).toString();
        }
        return taskDescriptions;
    }

    /**
     * Given an arbitrary number of keywords,
     * returns tasks' strings that contains at least one of those keywords.
     *
     * @param keywords Keywords that tasks must contain.
     * @return Array of task strings.
     */
    public String[] findTasks(String... keywords) {
        ArrayList<String> matchingTasks = new ArrayList<>();
        for (Task task : this.tasks) {
            if (Arrays.stream(keywords).anyMatch(task.toString()::contains)) {
                matchingTasks.add(task.toString());
            }
        }
        return matchingTasks.toArray(String[]::new);
    }
}
