package org.pharmgkb.parsers.escape;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Rfc3986Escaper}.
 * @author Douglas Myers-Turnbull
 */
public class Rfc3986EscaperTest {

	@Test
	public void testEscape() {
		Rfc3986Escaper escaper = new Rfc3986Escaper.Builder().addChars('%', ';', ':').build();
		assertEquals("abc123%3b55", escaper.escape("abc123;55"));
		assertEquals("abc123%3b%3b55", escaper.escape("abc123;;55"));
		assertEquals("abc%3a123%3b%3b55", escaper.escape("abc:123;;55"));
	}

	@Test
	public void testEscapeInverse() {
		Rfc3986Escaper escaper = new Rfc3986Escaper.Builder()
				.inverseLegality()
				.addChars('a', 'b', 'c')
				.build();
		assertEquals("abc%31", escaper.escape("abc1"));
		assertEquals("%39abc%31", escaper.escape("9abc1"));
	}

	@Test
	public void testUnescape() {
		Rfc3986Escaper escaper = new Rfc3986Escaper.Builder().addChars('%', ';', ':').build();
		assertEquals("abc123;55", escaper.unescape("abc123%3b55"));
		assertEquals("abc123;;55", escaper.unescape("abc123%3b%3b55"));
		assertEquals("abc:123;;55", escaper.unescape("abc%3a123%3b%3b55"));
	}

	@Test
	public void testUnescapeInverse() {
		Rfc3986Escaper escaper = new Rfc3986Escaper.Builder()
				.inverseLegality()
				.addChars('a', 'b', 'c')
				.build();
		assertEquals("abc1", escaper.unescape("abc%31"));
		assertEquals("9abc1", escaper.unescape("%39abc%31"));
	}
}