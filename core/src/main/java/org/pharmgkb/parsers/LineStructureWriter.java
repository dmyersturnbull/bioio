package org.pharmgkb.parsers;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A parser that transforms a data structure into a stream of lines.
 * @author Douglas Myers-Turnbull
 */
public interface LineStructureWriter<S> extends Function<S, Stream<String>> {

	default void writeToFile(@Nonnull S structure, @Nonnull Path file) throws IOException {
		writeToFile(structure, file.toFile());
	}
	default void writeToFile(@Nonnull S structure, @Nonnull File file) throws IOException {
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)), true)) {
			apply(structure).forEach(pw::println);
		}
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
