/** Represents a task that can be stored in the task list. */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        validateArguemnt(description, "Description");
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public String getDescription() {
        return this.description;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return this.isDone;
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsUndone() {
        this.isDone = false;
    }

    protected static void validateArguemnt(String input, String type) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(type + "cannot be empty.");
        }
    }
}
