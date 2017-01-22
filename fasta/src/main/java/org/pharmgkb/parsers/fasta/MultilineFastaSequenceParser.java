package org.pharmgkb.parsers.fasta;

import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.MultilineParser;
import org.pharmgkb.parsers.ObjectBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Using {@link #parseAll(Stream)}, {@link #collectAll(File)} and their overloaded methods is strongly recommended.
 * @author Douglas Myers-Turnbull
 */
@NotThreadSafe
public class MultilineFastaSequenceParser implements MultilineParser<FastaSequence> {

	private final boolean m_allowComments;
	private final boolean m_allowBlankLines;
	private final String m_terminationString;

	private String currentHeader = null;
	private String currentSequence = null;
	private boolean m_hitTerm = false;
	private boolean m_hasTerm = false;

	private static int m_nLines = 0;

	public MultilineFastaSequenceParser(@Nonnull Builder builder) {
		m_allowComments = builder.m_allowComments;
		m_allowBlankLines = builder.m_allowBlankLines;
		m_terminationString = builder.m_terminationString;
	}

	@Nonnull
	public Runnable getCloseHandler() {
		return () -> {
			if (!m_hitTerm) throw new IllegalStateException("Stream never hit terminal sequence; appears not to have completed");
		};
	}

	@Nonnull
	private Stream<String> appendTermination(@Nonnull Stream<String> stream) {
		m_hasTerm = true;
		return Stream.concat(stream, Stream.of(m_terminationString));
	}

	@Nonnull
	@Override
	public Stream<FastaSequence> parseAll(@Nonnull Stream<String> stream) throws IOException, BadDataFormatException {
		return appendTermination(stream).flatMap(this);
	}

	/**
	 * <strong>Calling {@link #parseAll(Stream)} instead is strongly recommended.</strong>
	 */
	@Nonnull
	@Override
	public Stream<FastaSequence> apply(@Nonnull String line) {

		if (!m_hasTerm) {
			throw new IllegalStateException("Must call with parseAll or collectAll rather than apply");
		}

		m_nLines++;

		if (line.equals(m_terminationString)) {
			m_hitTerm = true;
			if (currentHeader == null) { // happens if we read an empty source
				return Stream.empty();
			}
			return Stream.of(new FastaSequence(currentHeader, currentSequence));
		}

		FastaSequence seq = readNext(line);
		if (seq == null) return Stream.empty();
		return Stream.of(seq);
	}

	@Nullable
	private FastaSequence readNext(String line) {

		if (line.startsWith(">")) {

			FastaSequence seq = null;

			// currentHeader is null only for the first header
			// after reading each subsequent header, we know we've finished the sequence
			// so, return that
			if (currentHeader != null) {
				try {
					seq = new FastaSequence(currentHeader, currentSequence);
				} catch (IllegalArgumentException e) {
					throw new BadDataFormatException(e);
				}
			}

			// reset
			currentHeader = line.substring(1);
			currentSequence = "";

			return seq;

		} else if (!m_allowBlankLines || !line.isEmpty()) { // don't trim
			if (!m_allowComments || !line.startsWith(";")) {
				if (currentHeader == null) {
					throw new BadDataFormatException("Read sequence line \"" + line + "\" without header");
				}
				currentSequence += line;
			}
		}
		return null;
	}

	@Override
	public long nLinesProcessed() {
		return m_nLines;
	}

	/**
	 * Throws an {@link IllegalStateException} if the termination sequence of the stream was not reached.
	 * <em>Do not rely on this method.</em>
	 * Note that {@link java.util.stream.BaseStream} is {@link AutoCloseable},
	 * so you can use it with try-with-resources and {@link Stream#onClose(Runnable)} if you really feel the need:
	 * {@code
	 * Parser parser = new MultilineFastaSequenceParser.Builder().build();
	 * try (Stream<String> strings = Files.lines(file).onClose(parser.getCloseHandler()) { // <--- note the onClose()
	 *     Stream<FastaSequence> seqs = strings.flatMap(parser);
	 * }
	 * }
	 * The try-with-resources above will throw an {@link IllegalStateException} because the terminal sequence
	 * was never reached (assuming it wasn't somehow read from the text file).
	 * Instead, call {@link #appendTermination(Stream)} (or {@link Stream#concat(Stream, Stream)}) on the stream.
	 * These methods work even if {@link Builder#setTermination(char)} is called.
	 *
	 * <em>Better yet, just use {@link #parseAll(Stream)}, {@link #collectAll(File)}, etc.</em> These methods will
	 * automatically call {@link #appendTermination(Stream)}.
	 * {@code
	 * Stream<FastaSequence> = new MultilineFastaSequenceParser.Builder().build().parseAll(file);
	 * }
	 * @throws IllegalStateException If the termination was not reached
	 */
	@Override
	protected void finalize() throws Throwable {
		/*
		DO NOT REMOVE THIS METHOD.
		This check does no harm and might catch a bug.
		 */
		super.finalize();
		getCloseHandler().run();
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<MultilineFastaSequenceParser> {

		private boolean m_allowComments = false;
		private boolean m_allowBlankLines = false;
		private String m_terminationString = String.valueOf((char)0x00);

		/**
		 * Skips lines that start with ";". Otherwise, those lines are treated as part of the sequence.
		 */
		@Nonnull
		public Builder allowComments() {
			m_allowComments = true;
			return this;
		}

		/**
		 * Skips lines that are completely empty (just a newline, not even whitespace).
		 */
		@Nonnull
		public Builder allowBlankLines() {
			m_allowBlankLines = true;
			return this;
		}

		/**
		 * @see #setTermination(String)
		 */
		@Nonnull
		public Builder setTermination(char terminationChar) {
			return setTermination(String.valueOf(terminationChar));
		}

		/**
		 * Use this sequence to denote the end of the stream.
		 * <strong>This sequence must be present as the last element; if it is not, {@link Stream#flatMap(Function)}
		 * will not add the last {@link FastaSequence}.</strong>
		 */
		@Nonnull
		public Builder setTermination(@Nonnull String terminationString) {
			Preconditions.checkArgument(!terminationString.contains("\n") && !terminationString.contains("\r"),
					"Termination symbol cannot contain \\n or \\r");
			m_terminationString = terminationString;
			return this;
		}

		@Nonnull
		@Override
		public MultilineFastaSequenceParser build() {
			return new MultilineFastaSequenceParser(this);
		}
	}

}
