package org.pharmgkb.parsers;

import javax.annotation.Nonnull;

/**
 * A way to escape and unescape text.
 * {@code escaper.escape(escaper.unescaper(string))} must be the same as
 * {@code escaper.unescape(escaper.escape(string))}, which is just {@code string}.
 * @author Douglas Myers-Turnbull
 */
public interface CharacterEscaper {

	@Nonnull String escape(@Nonnull String string);

	@Nonnull String unescape(@Nonnull String string);

}
