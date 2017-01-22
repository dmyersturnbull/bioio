package org.pharmgkb.parsers.escape;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Function;

/**
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class BackslashEscaper extends IllegalCharacterEscaper {

	private BackslashEscaper(@Nonnull Builder builder) {
		super(builder.m_inverse, ImmutableSet.copyOf(builder.m_chars));
	}

	@Nonnull
	@Override
	protected Function<Character, String> encoder() {
		return c -> "\\" + c;
	}

	@Nonnull
	@Override
	protected Function<String, Character> unencoder() {
		return s -> s.charAt(s.length() - 1);
	}

	@NotThreadSafe
	public static class Builder extends IllegalCharacterEscaper.Builder<BackslashEscaper, Builder> {
		@Nonnull
		@Override
		public BackslashEscaper build() {
			Preconditions.checkState(!m_inverse && m_chars.contains('\\') || m_inverse && !m_chars.contains('\\'),
					getClass().getSimpleName() + " MUST escape \\ for escape and unescape to be inverses");
			return new BackslashEscaper(this);
		}
	}

}