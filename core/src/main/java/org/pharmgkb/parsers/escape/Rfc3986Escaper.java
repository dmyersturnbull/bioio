package org.pharmgkb.parsers.escape;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Escapes and unescapes some characters with RF3986 percent-encoding.
 * @author Douglas Myers-Turnbull
 */
public class Rfc3986Escaper extends IllegalCharacterEscaper {

	private static final Pattern sf_encodedPattern = Pattern.compile("%(?:\\d|[A-Fa-f]){2}");

	public Rfc3986Escaper(boolean inverseIllegality, @Nonnull char... illegalChars) {
		super(inverseIllegality, illegalChars);
	}

	public Rfc3986Escaper(boolean inverseIllegality, @Nonnull Set<Character> illegalChars) {
		super(inverseIllegality, illegalChars);
	}

	@Nonnull
	@Override
	protected Function<Character, String> encoder() {
		return c -> {
			String encoded = String.format("%04x", (int) c);
			Preconditions.checkArgument(encoded.length() == 4);
			Preconditions.checkArgument(encoded.substring(0, 2).equals("00"));
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
}