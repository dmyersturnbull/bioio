package org.pharmgkb.parsers;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Escapes and unescapes characters in a set of illegal characters.
 * @author Douglas Myers-Turnbull
 */
public abstract class IllegalCharacterEscaper implements CharacterEscaper {

	private final Set<Character> m_illegalChars;

	private final boolean m_inverseIllegality;

	@Nonnull
	protected abstract Function<Character, String> encoder();

	@Nonnull
	protected abstract Function<String, Character> unencoder();

	/**
	 * @param inverseIllegality If true, escapes and unescapes characters <em>not</em> in the list instead
	 */
	public IllegalCharacterEscaper(boolean inverseIllegality, @Nonnull char... illegalChars) {
		m_inverseIllegality = inverseIllegality;
		m_illegalChars = new HashSet<>();
		for (char c : illegalChars) {
			m_illegalChars.add(c);
		}
	}

	/**
	 * @param inverseIllegality If true, escapes and unescapes characters <em>not</em> in the list instead
	 */
	public IllegalCharacterEscaper(boolean inverseIllegality, @Nonnull Set<Character> illegalChars) {
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
					Preconditions.checkArgument(Character.isDigit(c), "Bad unescaped string " + string);
					onDigit.add(c);
					if (onDigit.size() > 1) {
						String unescapedHex = "%" + String.valueOf(onDigit.get(0)) + String.valueOf(onDigit.get(1));
						sb.append(unencoder().apply(unescapedHex));
						onDigit = null;
					}

				} else {
					Preconditions.checkArgument(m_illegalChars.contains(c), "Bad unescaped string " + string);
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

}
