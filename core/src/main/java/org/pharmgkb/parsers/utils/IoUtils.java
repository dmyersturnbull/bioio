package org.pharmgkb.parsers.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

	public static int getResponseCode(String url) {
		return getResponseCode(getUrl(url));
	}

	public static int getResponseCode(URL url) {
		try {
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			if (huc.getResponseCode() < 400 || huc.getResponseCode() >= 500) {
				throw new IOException("Response code is " + huc.getResponseCode());
			}
			return huc.getResponseCode();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static QuietBufferedReader openUtf8ReaderFromUrl(String url) throws UncheckedIOException {
		return openUtf8ReaderFromUrl(getUrl(url));
	}

	public static QuietBufferedReader openUtf8ReaderFromUrl(URL url) throws UncheckedIOException {
		try {
			return new QuietBufferedReader((new InputStreamReader(new GZIPInputStream(url.openStream()))));
		}  catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static URL getUrl(String url) throws UncheckedIOException {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new UncheckedIOException(e);
		}
	}

    public static Stream<String> readUtf8Lines(Path path) throws UncheckedIOException {
		try (QuietBufferedReader br = IoUtils.openUtf8Reader(path)) {
			return br.streamLinesQuietly();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static void writeUtf8Lines(Path path, Stream<String> lines) throws UncheckedIOException {
		PrintWriter pw = openUtf8Writer(path, false);
		lines.forEach(pw::println);
	}

	public static void appendUtf8Lines(Path path, Stream<String> lines) throws UncheckedIOException {
		PrintWriter pw = openUtf8Writer(path, true);
		lines.forEach(pw::println);
	}

	public static PrintWriter openUtf8Writer(Path path, boolean append) throws UncheckedIOException {
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

	public static QuietBufferedReader openUtf8Reader(Path path) throws UncheckedIOException {
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

	public static void writeUtf8Lines(File file, Stream<String> lines) throws UncheckedIOException {
		writeUtf8Lines(file.toPath(), lines);
	}
	public static PrintWriter openUtf8Writer(File file, boolean append) throws UncheckedIOException {
		return openUtf8Writer(file.toPath(), append);
	}
	public static QuietBufferedReader openUtf8Reader(File file) throws UncheckedIOException {
		return openUtf8Reader(file.toPath());
	}

}
