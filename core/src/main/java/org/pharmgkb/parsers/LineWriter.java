package org.pharmgkb.parsers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.stream.StreamSupport
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Counterpart to {@link LineParser}.
 *
 * @author Douglas Myers-Turnbull
 */
public interface LineWriter<T> extends Function<T, String> {

	default void appendToFile(@Nonnull Stream<T> stream, @Nonnull Path file) throws IOException {
		appendToFile(stream, file.toFile());
	}
	default void appendToFile(@Nonnull Iterable<T> lines, @Nonnull Path file) throws IOException {
		appendToFile(lines, file.toFile());
	}
	default void appendToFile(@Nonnull Iterable<T> lines, @Nonnull File file) throws IOException {
		appendToFile(StreamSupport.stream(lines.spliterator(), false), file);
	}
	default void appendToFile(@Nonnull Stream<T> stream, @Nonnull File file) throws IOException {
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true)) {
			stream.forEach(pw::println);
		}
	}
	default void writeToFile(@Nonnull Stream<T> stream, @Nonnull Path file) throws IOException {
		writeToFile(stream, file.toFile());
	}
	default void writeToFile(@Nonnull Iterable<T> lines, @Nonnull Path file) throws IOException {
		writeToFile(lines, file.toFile());
	}
	default void writeToFile(@Nonnull Iterable<T> lines, @Nonnull File file) throws IOException {
		writeToFile(StreamSupport.stream(lines.spliterator(), false), file);
	}
	default void writeToFile(@Nonnull Stream<T> stream, @Nonnull File file) throws IOException {
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)), true)) {
			stream.forEach(pw::println);
		}
	}
	default void writeToFile(@Nonnull Collection<T> lines, @Nonnull Path file) throws IOException {
		try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
			writeAll(lines.stream())
					.forEach(pw::println);
		}
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
