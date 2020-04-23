package org.pharmgkb.parsers.escape;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Escapes and unescapes some characters with RF3986 percent-encoding.
 * For an example use, this Rfc3986Escaper escapes every character that would not be in a simple floating-point number;
 * that is, does not match {@code [-.0-9]}.
 * <code>
 * Rfc3986Escaper escaper = new Rfc3986Escaper.Builder()
 * 		.inverseLegality() // escape everything NOT in the set instead
 * 		.addChars('.', '-')
 * 		.addCharRange(0x30, 0x39) // 0-9
 * 		.build();
 * String escaped = escaper.escape("ab-1.332cd"); // "%61;%62;-1..332%63;%64;"
 * String unescaped = escaper.unescape(escaped) // "ab-1.332cd"
 * </code>
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class Rfc3986Escaper extends IllegalCharacterEscaper {

	private static final Pattern sf_encodedPattern = Pattern.compile("%(?:\\d|[A-Fa-f]){2}");

	private Rfc3986Escaper(@Nonnull Builder builder) {
		super(builder.m_inverse, ImmutableSet.copyOf(builder.m_chars));
	}

	@Nonnull
	@Override
	protected Function<Character, String> encoder() {
		return c -> {
			String encoded = String.format("%04x", (int) c);
			Preconditions.checkArgument(encoded.length() == 4);
			Preconditions.checkArgument(encoded.startsWith("00"));
			return "%" + encoded.substring(2);
		};
	}

	@Nonnull
	@Override
	protected Function<String, Character> unencoder() {
		return s -> {
			Preconditions.checkArgument(sf_encodedPattern.matcher(s).matches(), "Bad RFC3986-encoded string " + s);
			return (char)Integer.parseInt(s.substring(1), 16);
		};
	}

	@NotThreadSafe
	public static class Builder extends IllegalCharacterEscaper.Builder<Rfc3986Escaper, Builder> {
		@Nonnull
		@Override
		public Rfc3986Escaper build() {
			Preconditions.checkState(!m_inverse && m_chars.contains('%') || m_inverse && !m_chars.contains('%'),
					getClass().getSimpleName() + " MUST escape % for escape and unescape to be inverses");
			return new Rfc3986Escaper(this);
		}
	}

}