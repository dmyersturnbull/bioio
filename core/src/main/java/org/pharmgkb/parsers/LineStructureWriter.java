package org.pharmgkb.parsers;

import org.pharmgkb.parsers.utils.IoUtils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A parser that transforms a data structure into a stream of lines.
 * @author Douglas Myers-Turnbull
 */
public interface LineStructureWriter<S> extends Function<S, Stream<String>> {

	default void writeToFile(@Nonnull S structure, @Nonnull Path file) throws UncheckedIOException {
		writeToFile(structure, file.toFile());
	}
	default void writeToFile(@Nonnull S structure, @Nonnull File file) throws UncheckedIOException {
		IoUtils.writeUtf8Lines(file, apply(structure));
	}

	/**
	 * @return The total number of lines this writer processed since its creation
	 */
	@Nonnegative
	long nLinesProcessed();

	@Override
	@Nonnull
	Stream<String> apply(@Nonnull S structure);
}
