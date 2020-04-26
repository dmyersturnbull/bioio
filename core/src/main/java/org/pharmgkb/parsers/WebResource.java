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

	protected WebResource(@Nonnull String url, boolean isGzip, @Nonnull Optional<Path> cachePath) {
		this(IoUtils.getUrl(url), isGzip, cachePath);
	}
	protected WebResource(@Nonnull URL url, boolean isGzip, @Nonnull Optional<Path> cachePath) {
		Path path = cachePath.orElse(null);
		if (path != null && isGzip && !path.endsWith(".gzip") && !path.endsWith(".gz")) {
			sf_logger.warn("Modifying cache path " + path + " to end with .gz");
			this.m_path = Paths.get(path.toString() + ".gz");
		} else if (path != null && !isGzip && (path.endsWith(".gzip") || path.endsWith(".gz"))) {
			sf_logger.warn("Modifying cache path " + path + " to end with .txt");
			this.m_path = Paths.get(path.toString() + ".txt");
		} else {
			this.m_path = path;
		}
		this.m_url = url;
		this.m_isGzip = isGzip;
	}

	public URL getUrl() {
		return m_url;
	}

	@Nonnull
	public Optional<Path> getCachePath() {
		return Optional.ofNullable(m_path);
	}

	public boolean hasCache() {
		return this.m_path != null;
	}
	public boolean isCached() {
		return this.m_path != null && this.m_path.toFile().exists() && this.m_path.toFile().length() > 0;
	}

	@Nonnull
	public WebResource<T> cacheTo(@Nonnull Path path) {
		return new WebResource<>(m_url, m_isGzip, Optional.ofNullable(path));
	}

	@Nonnull
	public Stream<String> readLines() throws UncheckedIOException  {
		if (this.hasCache()) {
			if (!this.isCached()) {
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
