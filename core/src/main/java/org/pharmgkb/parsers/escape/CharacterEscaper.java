package org.pharmgkb.parsers.escape;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A way to escape and unescape text.
 * {@code escaper.escape(escaper.unescaper(string))} must be the same as
 * {@code escaper.unescape(escaper.escape(string))}, which is just {@code string}.
 * @author Douglas Myers-Turnbull
 */
public interface CharacterEscaper {

	@Nonnull
	default Optional<String> escape(@Nonnull Optional<String> string) {
		return string.map(this::escape);
	}

	@Nonnull String escape(@Nonnull String string);

	@Nonnull
	default Optional<String> unescape(@Nonnull Optional<String> string) {
		return string.map(this::unescape);
	}

	@Nonnull String unescape(@Nonnull String string);

}
