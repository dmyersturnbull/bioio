package org.pharmgkb.parsers;

import org.pharmgkb.parsers.utils.IoUtils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Counterpart to {@link LineParser}.
 *
 * @author Douglas Myers-Turnbull
 */
public interface LineWriter<T> extends Function<T, String> {

	default void appendToFile(@Nonnull Stream<T> stream, @Nonnull Path file) throws UncheckedIOException {
		appendToFile(stream, file.toFile());
	}
	default void appendToFile(@Nonnull Iterable<T> lines, @Nonnull Path file) throws UncheckedIOException {
		appendToFile(lines, file.toFile());
	}
	default void appendToFile(@Nonnull Iterable<T> lines, @Nonnull File file) throws UncheckedIOException {
		appendToFile(StreamSupport.stream(lines.spliterator(), false), file);
	}
	default void appendToFile(@Nonnull Stream<T> stream, @Nonnull File file) throws UncheckedIOException {
		IoUtils.appendUtf8Lines(file.toPath(), stream.map(Object::toString));
	}
	default void writeToFile(@Nonnull Stream<T> stream, @Nonnull Path file) throws UncheckedIOException {
		writeToFile(stream, file.toFile());
	}
	default void writeToFile(@Nonnull Iterable<T> lines, @Nonnull Path file) throws UncheckedIOException {
		writeToFile(lines, file.toFile());
	}
	default void writeToFile(@Nonnull Iterable<T> lines, @Nonnull File file) throws UncheckedIOException {
		writeToFile(StreamSupport.stream(lines.spliterator(), false), file);
	}
	default void writeToFile(@Nonnull Stream<T> stream, @Nonnull File file) throws UncheckedIOException {
		IoUtils.writeUtf8Lines(file.toPath(), stream.map(Object::toString));
	}
	default void writeToFile(@Nonnull Collection<T> lines, @Nonnull Path file) throws UncheckedIOException {
		IoUtils.writeUtf8Lines(file, lines.stream().map(Object::toString));
	}

	/**
	 * Override this to add post- or pre- validation or processing.
	 */
	default @Nonnull Stream<String> writeAll(@Nonnull Stream<T> stream) {
		return stream.map(this);
	}

	/**
	 * @return The total number of lines this writer processed since its creation
	 */
	@Nonnegative
	long nLinesProcessed();

	@Override
	@Nonnull String apply(@Nonnull T t);
}
