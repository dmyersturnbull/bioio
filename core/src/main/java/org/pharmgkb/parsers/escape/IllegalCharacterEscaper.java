package org.pharmgkb.parsers.escape;

import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.ObjectBuilder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Escapes and unescapes characters in a set of illegal characters.
 * Can also escape and unescape every character that is <em>not</em> in the set.
 * </code>
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public abstract class IllegalCharacterEscaper implements CharacterEscaper {

	private static final Pattern sf_hexDigit = Pattern.compile("\\d|[A-Fa-f]");

	private final Set<Character> m_illegalChars;

	private final boolean m_inverseIllegality;

	@Nonnull
	protected abstract Function<Character, String> encoder();

	@Nonnull
	protected abstract Function<String, Character> unencoder();

	/**
	 * @param inverseIllegality If true, escapes and unescapes characters <em>not</em> in the list instead
	 */
	protected IllegalCharacterEscaper(boolean inverseIllegality, @Nonnull char... illegalChars) {
		m_inverseIllegality = inverseIllegality;
		m_illegalChars = new HashSet<>();
		for (char c : illegalChars) {
			m_illegalChars.add(c);
		}
	}

	/**
	 * @param inverseIllegality If true, escapes and unescapes characters <em>not</em> in the list instead
	 */
	protected IllegalCharacterEscaper(boolean inverseIllegality, @Nonnull Set<Character> illegalChars) {
		m_inverseIllegality = inverseIllegality;
		m_illegalChars = illegalChars;
	}

	@Nonnull
	@Override
	public String escape(@Nonnull String string) {
		StringBuilder sb = new StringBuilder();
		for (Character c : string.toCharArray()) {
			if (m_illegalChars.contains(c) ^ m_inverseIllegality) {
				sb.append(encoder().apply(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@Nonnull
	@Override
	public String unescape(@Nonnull String string) {

		if (m_inverseIllegality) {

			StringBuilder sb = new StringBuilder(); // the string we'll build up

			// the digits for a hex we've been reading
			// null if we're not reading a digit
			// empty if we've only read the %
			List<Character> onDigit = null;

			for (Character c : string.toCharArray()) {

				if (c == '%') {
					onDigit = new ArrayList<>();

				} else if (onDigit != null) {
					Preconditions.checkArgument(sf_hexDigit.matcher(String.valueOf(c)).matches(), "Bad escaped string " + string);
					onDigit.add(c);
					if (onDigit.size() > 1) {
						String unescapedHex = "%" + String.valueOf(onDigit.get(0)) + String.valueOf(onDigit.get(1));
						sb.append(unencoder().apply(unescapedHex));
						onDigit = null;
					}

				} else {
					Preconditions.checkArgument(m_illegalChars.contains(c), "Bad escaped string " + string);
					sb.append(c);
				}
			}

			return sb.toString();

		} else {

			String replaced = string;
			for (char ill : m_illegalChars) {
				replaced = replaced.replaceAll(Pattern.quote(encoder().apply(ill)), String.valueOf(ill));
			}
			return replaced;

		}
	}

	@NotThreadSafe
	protected abstract static class Builder<T, B extends Builder<?, ?>> implements ObjectBuilder<T> {

		protected boolean m_inverse = false;
		protected Set<Character> m_chars = new HashSet<>();

		@SuppressWarnings("unchecked")
		@Nonnull
		public B inverseLegality() {
			m_inverse = true;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		@Nonnull
		public B addChars(@Nonnull Collection<Character> chars) {
			m_chars.addAll(chars);
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		@Nonnull
		public B addChars(@Nonnull char... chars) {
			for (char c : chars) m_chars.add(c);
			return (B) this;
		}

		/**
		 * Adds the characters from {@code start} to {@code end}, inclusive.
		 */
		@SuppressWarnings("unchecked")
		@Nonnull
		public B addCharRange(char start, char end) {
			for (int i = start; i < end; i++) {
				m_chars.add((char) i);
			}
			return (B) this;
		}

		/**
		 * Adds the characters from {@code start} to {@code end}, inclusive.
		 * Simply casts the ints to chars.
		 */
		@Nonnull
		public B addCharRange(int start, int end) {
			return addCharRange((char) start, (char) end);
		}

	}

}
