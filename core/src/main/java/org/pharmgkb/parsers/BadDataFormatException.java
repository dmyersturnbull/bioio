package org.pharmgkb.parsers;

import javax.annotation.Nonnull;

/**
 * Data was not formatted correctly.
 * @author Douglas Myers-Turnbull
 */
public class BadDataFormatException extends RuntimeException {

	public BadDataFormatException(@Nonnull String message) {
		super(message);
	}

	public BadDataFormatException(@Nonnull String message, @Nonnull Throwable cause) {
		super(message, cause);
	}

	public BadDataFormatException(@Nonnull Throwable cause) {
		super(cause);
	}
}
