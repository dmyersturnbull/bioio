package org.pharmgkb.parsers;

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
	default Optional<String> escape(Optional<String> string) {
		if (string.isPresent()) {
			return escape(string);
		}
		return string;
	}

	@Nonnull String escape(@Nonnull String string);

	@Nonnull
	default Optional<String> unescape(Optional<String> string) {
		if (string.isPresent()) {
			return unescape(string);
		}
		return string;
	}

	@Nonnull String unescape(@Nonnull String string);

}
