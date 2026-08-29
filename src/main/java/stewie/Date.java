package stewie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import java.util.List;
import java.util.Locale;

/**
 * Represents a date.
 *
 * Date uses Java LocalDate object.
 */

public class Date {
    private final LocalDate date;
    private static final List<DateTimeFormatter> FORMATTERS = List.of(
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d-M-uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d.M.uuuu").withResolverStyle(ResolverStyle.STRICT),

        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMM uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT),

        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMMM uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT),

        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM d, uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT),

        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMMM d, uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT)
    );

    /**
     * Instantiate Date object
     * @param s String object
     */
    public Date(String s) {
        this.date = this.parseDate(s);
    }

    /**
     * Returns a LocalDate object by parsing a string.
     * If the given string is null or empty, it throws an IllegalArgumentException
     *
     * @param d Date in string
     * @return LocalDate object that represents the date
     * @throws IllegalArgumentException if the string is null, empty, or cannot be parsed.
     */
    private LocalDate parseDate(String d) {
        if (d == null || d.isBlank()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }

        String value = d.trim();

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Date format is not recognized");
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return date.format(formatter);
    }
}
