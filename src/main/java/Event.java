/** A task that takes place between a start time and an end time. */
public class Event extends Task {
    private String from;
    private String to;

    public Event(String description, String from, String to) throws IllegalArgumentException{
        validateArguemnt(from, "Event start time");
        validateArguemnt(to, "Event end time");
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Returns the event's start time. */
    public String getFrom() {
        return this.from;
    }

    /** Returns the event's end time. */
    public String getTo() {
        return this.to;
    }

    @Override
    public String toString() {
        return String.format("[E] [%s] %s (from: %s to: %s)", super.getStatusIcon(),
                super.getDescription(),
                this.from,
                this.to
        );
    }
}
