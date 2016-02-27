package org.pharmgkb.parsers;

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

  default void writeToFile(@Nonnull Collection<T> lines, @Nonnull Path file) throws IOException {
    try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
      lines.stream()
          .map(this)
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
