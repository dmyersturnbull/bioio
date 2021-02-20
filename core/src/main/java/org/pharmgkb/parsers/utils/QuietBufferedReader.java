package org.pharmgkb.parsers.utils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.stream.Stream;


public class QuietBufferedReader extends BufferedReader {

	public static final int DEFAULT_BUFFER_SIZE = 8192;

	private final int bufferSize;

	public QuietBufferedReader(@Nonnull Reader in, @Nonnegative int sz) {
		super(in, sz);
		bufferSize = sz;
	}
	public QuietBufferedReader(@Nonnull Reader in) {
		this(in, DEFAULT_BUFFER_SIZE);
	}

	@Nonnull
	public Stream<String> streamLinesQuietly() throws UncheckedIOException {
		// already implemented!
		return lines();
	}

	@Nonnull
	public Stream<String> streamChunksQuietly(@Nonnegative int nChars) throws UncheckedIOException {
		return Stream.generate(() -> readCharsQuietly(nChars));
	}

	@Nonnull
	public String readLineQuietly() throws UncheckedIOException {
		try {
			return readLine();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public char readCharQuietly() throws UncheckedIOException {
		try {
			//noinspection NumericCastThatLosesPrecision
			return (char) read();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Nonnull
	public String readCharsQuietly(int nChars) throws UncheckedIOException {
		char[] z = new char[nChars];
		try {
			int val = read(z);
			return new String(z);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Nonnegative
	public int getBufferSize() {
		return bufferSize;
	}
}
