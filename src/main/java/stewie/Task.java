package stewie;

/**
 * Represent a Task.
 * A task has description and the status "isDone"
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Initialize the task with description
     *
     * @param description
     */
    public Task(String description) {
        validateArgument(description, "Description");
        this.description = description;
        this.isDone = false;
    }

    /**
     * Return status icon X
     *
     * @return String X if the task is done
     * @return Empty string if the task is not done
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Return the description of the task
     *
     * @return description String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Checks if the task is done
     *
     * @return boolean True or False
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Mark the task as done
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Mark the task as not done
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Check an argument is null or empty string
     * @param input Argument
     * @param type Type of argument
     * @throws IllegalArgumentException when the argument is null or empty string
     */
    protected static void validateArgument(String input, String type) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(type + "cannot be empty.");
        }
    }
}
