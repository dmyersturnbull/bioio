package org.pharmgkb.parsers;

import org.pharmgkb.parsers.utils.HttpHeadResponse;
import org.pharmgkb.parsers.utils.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A text resource that can be downloaded from a URL.
 * Works with either text or gzipped text.
 */
@Immutable
public class WebResource<T extends WebResource<T>> {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final URL m_url;
	private final boolean m_isGzip;
	private final Path m_path;

	protected WebResource(@Nonnull String url, boolean isGzip, @Nonnull Optional<? extends Path> cachePath) {
		this(IoUtils.getUrl(url), isGzip, cachePath);
	}
	protected WebResource(@Nonnull URL url, boolean isGzip, @Nonnull Optional<? extends Path> cachePath) {
		Path path = cachePath.orElse(null);
		if (path != null && isGzip && !path.endsWith(".gzip") && !path.endsWith(".gz")) {
			sf_logger.warn("Modifying cache path {} to end with .gz", path);
			m_path = Paths.get(path + ".gz");
		} else if (path != null && !isGzip && (path.endsWith(".gzip") || path.endsWith(".gz"))) {
			sf_logger.warn("Modifying cache path {} to end with .txt", path);
			m_path = Paths.get(path + ".txt");
		} else {
			m_path = path;
		}
		m_url = url;
		m_isGzip = isGzip;
	}

	public URL getUrl() {
		return m_url;
	}

	@Nonnull
	public Optional<Path> getCachePath() {
		return Optional.ofNullable(m_path);
	}

	public boolean hasCache() {
		return m_path != null;
	}
	public boolean isCached() {
		return m_path != null && m_path.toFile().exists() && m_path.toFile().length() > 0;
	}

	@Nonnull
	public WebResource<T> cacheTo(@Nonnull Path path) {
		return new WebResource<>(m_url, m_isGzip, Optional.ofNullable(path));
	}

	@Nonnull
	public Stream<String> readLines() throws UncheckedIOException  {
		if (hasCache()) {
			if (!isCached()) {
				IoUtils.downloadBytesTo(m_url, m_path);
			}
			return IoUtils.readUtf8Lines(m_path);
		} else if (m_isGzip) {
			return IoUtils.readGzipUtf8LinesFromUrl(m_url);
		} else {
			return IoUtils.readUtf8LinesFromUrl(m_url);
		}
	}

	@Nonnull
	public HttpHeadResponse queryHead() {
		return IoUtils.getHeadResponse(getUrl());
	}

}
