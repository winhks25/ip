/** A task that must be completed by a specified deadline. */
public class Deadline extends Task {
    private String deadline;

    public Deadline(String description, String deadline) {
        validateArguemnt(deadline, "Deadline date or time");
        super(description);
        this.deadline = deadline;
    }

    /** Returns the deadline used when this task was created. */
    public String getDeadline() {
        return this.deadline;
    }

    @Override
    public String toString() {
        return String.format("[D] [%s] %s (by: %s)", super.getStatusIcon(), super.getDescription(), this.deadline);
    }
}
