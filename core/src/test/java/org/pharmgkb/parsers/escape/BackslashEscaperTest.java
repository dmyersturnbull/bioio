package org.pharmgkb.parsers.escape;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link BackslashEscaper}.
 * @author Douglas Myers-Turnbull
 */
public class BackslashEscaperTest {

    @Test
    public void testEscape() {
        BackslashEscaper escaper = new BackslashEscaper.Builder().addChars('\\', ';').build();
        assertEquals("abc\\\\xyz", escaper.escape("abc\\xyz"));
    }

    @Test
    public void testUnescape() {
    }

}