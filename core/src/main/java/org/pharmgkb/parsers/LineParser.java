package org.pharmgkb.parsers;

import org.pharmgkb.parsers.utils.IoUtils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses a single line.
 * @author Douglas Myers-Turnbull
 */
public interface LineParser<R> extends Function<String, R> {

	@Nonnull
	default List<R> collectAll(@Nonnull File file) throws UncheckedIOException, BadDataFormatException {
		return collectAll(file.toPath());
	}

	@Nonnull
	default List<R> collectAll(@Nonnull Path file) throws UncheckedIOException, BadDataFormatException {
		return collectAll(IoUtils.readUtf8Lines(file));
	}

	@Nonnull
	default List<R> collectAll(@Nonnull Stream<String> stream) throws UncheckedIOException, BadDataFormatException {
		return parseAll(stream).collect(Collectors.toList());
	}

	@Nonnull
	default Stream<R> parseAll(@Nonnull File file) throws UncheckedIOException, BadDataFormatException {
		return parseAll(file.toPath());
	}

	@Nonnull
	default Stream<R> parseAll(@Nonnull Path file) throws UncheckedIOException, BadDataFormatException {
		return parseAll(IoUtils.readUtf8Lines(file));
	}

	/**
	 * For example:
	 * {@code
	 *     return stream.filter(s -> s.isEmpty() || s.startsWith("#")).map(this);
	 * }
	 * @throws UncheckedIOException For IO errors
	 * @throws BadDataFormatException For most formatting errors
	 */
	@Nonnull
	Stream<R> parseAll(@Nonnull Stream<String> stream) throws UncheckedIOException, BadDataFormatException;

	@Nonnull
	@Override
	R apply(@Nonnull String line) throws BadDataFormatException;

	/**
	 * @return The total number of lines this writer processed since its creation
	 */
	@Nonnegative
	long nLinesProcessed();

}
