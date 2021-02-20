package org.pharmgkb.parsers.utils;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * IO utilities.
 * Use {@link IoUtils#readUtf8Lines}, {@link IoUtils#writeUtf8Lines(Path, Stream)}, and similar methods
 * to read/write text or GZIP, depending on whether the filename extension ends with '.gz' or '.gzip'.
 */
public class IoUtils {

	@Nonnull
	public static HttpHeadResponse getHeadResponse(@Nonnull String url) throws UncheckedIOException, InvalidResponseException {
		return getHeadResponse(getUrl(url));
	}

	@Nonnull
	public static HttpHeadResponse getHeadResponse(@Nonnull URL url) throws UncheckedIOException, InvalidResponseException {
		try {
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("HEAD");
			huc.connect();
			int code = huc.getResponseCode();
			if (code < 400 || code >= 500) {
				throw new IOException("Response code is " + huc.getResponseCode());
			}
			return HttpHeadResponse.fromConnection(huc);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Downloads bytes.
	 * Works up to about 9 exabytes.
	 * @param url From
	 * @param path To
	 * @throws UncheckedIOException On any IO error
	 */
	public static void downloadBytesTo(@Nonnull URL url, @Nonnull Path path) throws UncheckedIOException {
		try {
			ReadableByteChannel in = Channels.newChannel(url.openStream());
			try (FileOutputStream out = new FileOutputStream(path.toFile())) {
				try (FileChannel channel = out.getChannel()) {
					channel.transferFrom(in, 0, Long.MAX_VALUE);
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static void downloadUtf8To(@Nonnull URL url, @Nonnull Path path) throws UncheckedIOException {
		// doesn't handle gzip input
		Stream<String> stream = readUtf8LinesFromUrl(url);
		writeUtf8Lines(path, stream);
	}

	@Nonnull
	public static Stream<String> readGzipUtf8LinesFromUrl(@Nonnull URL url) throws UncheckedIOException {
		try (QuietBufferedReader br = openGzipUtf8ReaderFromUrl(url)) {
			Stream<String> stream = br.streamLinesQuietly();
			return br.streamLinesQuietly();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Nonnull
	public static Stream<String> readUtf8LinesFromUrl(@Nonnull URL url) throws UncheckedIOException {
		try (QuietBufferedReader br = openUtf8ReaderFromUrl(url)) {
			Stream<String> stream = br.streamLinesQuietly();
			return br.streamLinesQuietly();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Nonnull
	public static QuietBufferedReader openGzipUtf8ReaderFromUrl(@Nonnull String url) throws UncheckedIOException {
		return openGzipUtf8ReaderFromUrl(getUrl(url));
	}

	@Nonnull
	public static QuietBufferedReader openUtf8ReaderFromUrl(@Nonnull String url) throws UncheckedIOException {
		return openUtf8ReaderFromUrl(getUrl(url));
	}

	@Nonnull
	public static QuietBufferedReader openGzipUtf8ReaderFromUrl(@Nonnull URL url) throws UncheckedIOException {
		try {
			return new QuietBufferedReader((new InputStreamReader(new GZIPInputStream(url.openStream()))));
		}  catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Nonnull
	public static QuietBufferedReader openUtf8ReaderFromUrl(@Nonnull URL url) throws UncheckedIOException {
		try {
			return new QuietBufferedReader((new InputStreamReader(url.openStream())));
		}  catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Nonnull
	public static URL getUrl(@Nonnull String url) throws UncheckedIOException {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Nonnull
	public static Stream<String> readUtf8Lines(@Nonnull Path path) throws UncheckedIOException {
		try (QuietBufferedReader br = IoUtils.openUtf8Reader(path)) {
			return br.streamLinesQuietly();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static void writeUtf8Lines(@Nonnull Path path, @Nonnull Stream<String> lines) throws UncheckedIOException {
		try (PrintWriter pw = openUtf8Writer(path, false)) {
			lines.forEach(pw::println);
		}
	}

	public static void appendUtf8Lines(@Nonnull Path path, @Nonnull Stream<String> lines) throws UncheckedIOException {
		try (PrintWriter pw = openUtf8Writer(path, true)) {
			lines.forEach(pw::println);
		}
	}

	@Nonnull
	public static PrintWriter openUtf8Writer(@Nonnull Path path, boolean append) throws UncheckedIOException {
		Charset encoding = StandardCharsets.UTF_8;
		try {
			if (path.endsWith(".gz") || path.endsWith(".gzip")) {
				return new PrintWriter(new BufferedWriter(new OutputStreamWriter(
						new GZIPOutputStream(new FileOutputStream(path.toFile(), append)), encoding
				)), true);
			} else {
				return new PrintWriter(new BufferedWriter(new FileWriter(path.toString(), encoding, false)), true);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Nonnull
	public static QuietBufferedReader openUtf8Reader(@Nonnull Path path) throws UncheckedIOException {
		Charset encoding = StandardCharsets.UTF_8;
		try {
			if (path.endsWith(".gz") || path.endsWith(".gzip")) {
				return new QuietBufferedReader(new InputStreamReader(
						new GZIPInputStream(new FileInputStream(path.toFile())),
						encoding
				));
			} else {
				return new QuietBufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), encoding));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Nonnull
	public static void writeUtf8Lines(@Nonnull File file, @Nonnull Stream<String> lines) throws UncheckedIOException {
		writeUtf8Lines(file.toPath(), lines);
	}
	@Nonnull
	public static PrintWriter openUtf8Writer(@Nonnull File file, boolean append) throws UncheckedIOException {
		return openUtf8Writer(file.toPath(), append);
	}
	@Nonnull
	public static QuietBufferedReader openUtf8Reader(@Nonnull File file) throws UncheckedIOException {
		return openUtf8Reader(file.toPath());
	}

}
