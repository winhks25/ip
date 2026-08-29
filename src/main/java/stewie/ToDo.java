package stewie;

/**
 * Represents a task of type ToDo.
 */
public class ToDo extends Task {
    /**
     * Initialize the ToDo task
     * @param description Description of the task
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return String.format("[T] [%s] %s", super.getStatusIcon(), super.getDescription());
    }
}
