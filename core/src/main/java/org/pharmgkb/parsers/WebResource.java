package org.pharmgkb.parsers;

import org.pharmgkb.parsers.utils.IoUtils;
import org.pharmgkb.parsers.utils.Try;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Function;

/**
 * A text resource that can be downloaded from a URL.
 * These are occasionally provided for convenience.
 */
public interface WebResource {

	URL getUrl();

	default BufferedReader newReader() throws UncheckedIOException {
		return IoUtils.openUtf8ReaderFromUrl(getUrl());
	}

	default Try<Integer, IOException> find() {
		return Try.attempt(() -> IoUtils.getResponseCode(getUrl()), IOException.class);
	}

}
