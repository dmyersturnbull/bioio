package org.pharmgkb.parsers.utils;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A wrapper around response codes and headers from a HttpURLConnection.
 * Only contains the relevant info and cannot connect/disconnect/fetch, for example.
 * Provides methods that assume valid HTTP responses and convert more intelligently than HttpURLConnection does.
 * Example:
 * {@code
 * HttpHeadResponse.fromConnection(
 * }
 */
@Immutable
public class HttpHeadResponse {

	private final URL m_url;
	private final int m_code;
	private final String m_message;
	private final Map<String, ? extends List<String>> m_headers;

	@Nonnull
	public static HttpHeadResponse fromConnection(@Nonnull HttpURLConnection connection) {
		int code;
		String message;
		try {
			code = connection.getResponseCode();
			message = connection.getResponseMessage();
		} catch (IOException e) {
			throw new IllegalStateException("fromConnection should be called only after getting a status code.", e);
		}
		if (code < 0) { // HttpURLConnection uses -1 to mean none found
			throw new InvalidResponseException("No response code was found");
		}
		return new HttpHeadResponse(connection.getURL(), code, message, connection.getHeaderFields());
	}

	public HttpHeadResponse(
			@Nonnull URL url,
			@Nonnegative int code, @Nonnull String message,
			@Nonnull Map<String, ? extends List<String>> headers
	) {
		m_url = url;
		m_code = code;
		m_message = message;
		m_headers = Map.copyOf(headers);
	}

	@Nonnegative
	public int getCode() {
		return m_code;
	}

	@Nonnull
	public String getMessage() {
		return m_message;
	}

	@Nonnull
	public URL getUrl() {
		return m_url;
	}

	@Nonnull
	public Optional<Long> getContentLength() throws InvalidResponseException {
		return getSingle("content-length").map(r ->
			Try.succeed(r, NumberFormatException.class)
			   .compose(Long::parseLong)
			   .require(v -> v < 0)
			   .orElseThrow(new InvalidResponseException("Failed to parse content-length " + r)));
	}

	@Nonnull
	public Optional<ZonedDateTime> getExpiration() {
		return getDatetime("expires");
	}

	@Nonnull
	public Optional<ZonedDateTime> getDate() {
		return getDatetime("date");
	}

	@Nonnull
	public Optional<ZonedDateTime> getLastModified() {
		return getDatetime("last-modified");
	}

	@Nonnull
	public Optional<String> getContentType() {
		return getSingle("content-type");
	}

	@Nonnull
	public Optional<String> getContentEncoding() {
		return getSingle("content-encoding");
	}

	@Nonnull
	public Optional<String> getSingle(@Nonnull String field) throws InvalidResponseException {
		List<String> values = m_headers.get(field);
		if (values.isEmpty()) {
			return Optional.empty();
		} else if (values.size() > 1) {
			throw new InvalidResponseException("Header " + field + " set " + values.size() + " times");
		}
		return Optional.of(values.get(0));
	}

	@Nonnull
	private Optional<ZonedDateTime> getDatetime(@Nonnull String field) throws InvalidResponseException {
		/*
			Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123
			Sunday, 06-Nov-94 08:49:37 GMT ; RFC 850, obsoleted by RFC 1036
			Sun Nov  6 08:49:37 1994       ; ANSI C's asctime() format
		 */
	   return getSingle("last-modified").map(z -> Try.
			   attempt(() -> ZonedDateTime.parse(z, DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz")), DateTimeParseException.class)
			   .recover(() -> ZonedDateTime.parse(z, DateTimeFormatter.ofPattern("EEEE, dd-MMM-yy HH:mm:ss zzz")))
			   .recover(() -> ZonedDateTime.parse(z + " GMT", DateTimeFormatter.ofPattern("EEE, MMM dd HH:mm:ss yyyy")))
			   .orElseThrow(new InvalidResponseException("Invalid date format " + z)));
		}

	@Nonnull
	public ImmutableMap<String, List<String>> getHeaders() {
		return ImmutableMap.copyOf(m_headers);
	}
}
