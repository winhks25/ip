package stewie;

/**
 * Represent an event.
 * An event has starting time (from) and ending time (to).
 */
public class Event extends Task {
    private Date from;
    private Date to;

    /**
     * Initialize the Event object.
     * @param description Description of the event.
     * @param from Start time of the event.
     * @param to End time of the event.
     * @throws IllegalArgumentException When the start time or end time is invalid.
     */
    public Event(String description, String from, String to)
            throws IllegalArgumentException {
        super(description);
        validateArgument(from, "Event start time");
        validateArgument(to, "Event end time");
        this.from = new Date(from);
        this.to = new Date(to);
    }

    /**
     * Returns start time of the event.
     * @return from Start time of the event.
     */
    public String getFrom() {
        return this.from.toString();
    }

    /**
     * Returns end time of the event.
     * @return to End time of the event.
     */
    public String getTo() {
        return this.to.toString();
    }

    @Override
    public String toString() {
        return String.format("[E] [%s] %s (from: %s to: %s)", super.getStatusIcon(),
                super.getDescription(),
                this.from.toString(),
                this.to.toString()
        );
    }
}
