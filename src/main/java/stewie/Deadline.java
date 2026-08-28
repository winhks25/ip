package stewie;

/**
 * Represent a task with a deadline.
 * Deadline class has a deadline of type Date.
 */
public class Deadline extends Task {
    private final Date deadline;

    /**
     * Initialize the Deadline object
     *
     * @param description Description of the deadline task
     * @param deadline Deadline date of the task
     */
    public Deadline(String description, String deadline) {
        validateArgument(deadline, "Deadline date or time");
        super(description);
        this.deadline = new Date(deadline);
    }

    /**
     * Returns the deadline in string type
     * @return deadline
     */
    public String getDeadline() {
        return this.deadline.toString();
    }

    @Override
    public String toString() {
        return String.format("[D] [%s] %s (by: %s)", super.getStatusIcon(), super.getDescription(), this.deadline.toString());
    }
}
