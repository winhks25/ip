public class Deadline extends Task {
    private String deadline;

    public Deadline(String description, String deadline) {
        validateArguemnt(deadline, "Deadline date or time");
        super(description);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return String.format("[D] [%s] %s (by: %s)", super.getStatusIcon(), super.getDescription(), this.deadline);
    }
}
