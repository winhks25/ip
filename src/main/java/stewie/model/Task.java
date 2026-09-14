package stewie.model;

/**
 * Represents a task.
 * A task has description and the status "isDone"
 */
public abstract class Task {
    private boolean isDone;
    private final String description;

    /**
     * Initialize the task with description
     *
     * @param description Nonempty task description without storage separators or control characters.
     */
    public Task(String description) {
        validateArgument(description, "Description");
        assert description != null && !description.isBlank()
                : "A task must have a non-blank description after validation";
        if (description.indexOf('|') >= 0 || description.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Descriptions cannot contain | or control characters.");
        }
        this.description = description.strip().replaceAll("(?U)\\s+", " ");
        this.isDone = false;
    }

    /**
     * Returns the status icon X.
     *
     * @return String X if the task is done, or an empty string otherwise
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the description of the task.
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
     * Checks task identity using type, description, and dates, regardless of completion status.
     * Description casing and repeated whitespace do not create a distinct task.
     *
     * @param other Task to compare with.
     * @return True when both tasks represent the same details.
     */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass()
                || !description.equalsIgnoreCase(other.description)) {
            return false;
        }
        if (this instanceof Deadline deadline && other instanceof Deadline otherDeadline) {
            return deadline.getDeadline().equals(otherDeadline.getDeadline());
        }
        if (this instanceof Event event && other instanceof Event otherEvent) {
            return event.getFrom().equals(otherEvent.getFrom()) && event.getTo().equals(otherEvent.getTo());
        }
        return true;
    }

    /**
     * Check an argument is null or empty string
     * @param input Argument
     * @param type Type of argument
     * @throws IllegalArgumentException when the argument is null or empty string
     */
    protected static void validateArgument(String input, String type) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(type + " cannot be empty.");
        }
    }
}
