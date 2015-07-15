package org.pharmgkb.parsers;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A parser that transforms a stream of lines into a data structure.
 * @author Douglas Myers-Turnbull
 */
public interface LineStructureParser<S> extends Function<Stream<String>, S> {

	@Nonnull
	default S parse(@Nonnull File file) throws IOException, BadDataFormatException {
		return parse(file.toPath());
	}

	@Nonnull
	default S parse(@Nonnull Path file) throws IOException, BadDataFormatException {
		return apply(Files.lines(file));
	}

	@Nonnull
	@Override
	S apply(Stream<String> stringStream);

	/**
	 * @return The total number of lines this parser processed since its creation
	 */
	@Nonnegative
	long nLinesProcessed();

}
