package stewie;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    @Test
    public void getTaskIndex_fail() {
        // inputs contains more than 2 words, test fails and return -1
        assertEquals(-1, Parser.getTaskIndex("mark task 1"));

        // second part of command is not integer, test fails and returns -1
        assertEquals(-1, Parser.getTaskIndex("delete one"));
    }

    @Test
    public void getTaskIndex_success() {
        // index is one less than task number
        // valid input with mark
        assertEquals(2, Parser.getTaskIndex("mark 3"));

        // valid input with delete and spaces before, between, and after
        assertEquals(4, Parser.getTaskIndex("     delete      5      "));

        // valid input with unmark and two-digit integer
        assertEquals(10, Parser.getTaskIndex("UNMARK 11"));
    }
}
