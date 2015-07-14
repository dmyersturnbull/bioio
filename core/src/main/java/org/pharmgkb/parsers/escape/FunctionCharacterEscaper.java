package org.pharmgkb.parsers.escape;

import org.pharmgkb.parsers.ObjectBuilder;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Takes a {@link java.util.function.Function}&lt;String, String&gt; to escape,
 * and a {@link java.util.function.Function}&lt;String, String&gt; to unescape.
 * @author Douglas Myers-Turnbull
 */
public class FunctionCharacterEscaper implements CharacterEscaper {

	private final Function<String, String> m_escaper;
	private final Function<String, String> m_unescaper;

	private FunctionCharacterEscaper(@Nonnull Builder builder) {
		m_escaper = builder.m_escaper;
		m_unescaper = builder.m_unescaper;
	}

	@Nonnull
	@Override
	public String escape(@Nonnull String string) {
		return m_escaper.apply(string);
	}

	@Nonnull
	@Override
	public String unescape(@Nonnull String string) {
		return m_unescaper.apply(string);
	}

	/**
	 * Call {@link #setEscaper(Function)} and {@link #setUnescaper(Function)} to set the functions.
	 * By default, the functions are both s -> s.
	 * For example:
	 * <code>
	 * CharacterEscaper escaper = new FunctionCharacterEscaper.Builder().build();
	 * escaper.escape("x"); // returns x
	 * </code>
	 */
	public static class Builder implements ObjectBuilder<FunctionCharacterEscaper> {

		private Function<String, String> m_escaper = s -> s;
		private Function<String, String> m_unescaper = s -> s;

		@Nonnull
		public Builder setEscaper(@Nonnull Function<String, String> escaper) {
			m_escaper = escaper;
			return this;
		}

		@Nonnull
		public Builder setUnescaper(@Nonnull Function<String, String> unescaper) {
			m_unescaper = unescaper;
			return this;
		}

		@Nonnull
		@Override
		public FunctionCharacterEscaper build() {
			return new FunctionCharacterEscaper(this);
		}
	}
}
