package org.pharmgkb.parsers.vcf;

import org.pharmgkb.parsers.vcf.model.VcfMetadataCollection;
import org.pharmgkb.parsers.vcf.model.VcfPosition;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A convenience class to read and entire VCF file, including metadata and data.
 * Example usage:
 * {@code
 * VcfFileParser parser = new VcfFileParser(file);
 * VcfMetadata metadata = parser.getMetadata();
 * Stream<VcfPosition> positions = parser.parse();
 * }
 * @author Douglas Myers-Turnbull
 * @deprecated
 */
@Deprecated
public class VcfFileParser {

	private long m_lineNumber = 0l;

	private Supplier<Stream<String>> m_supplier;
	private VcfMetadataCollection m_metadata;

	public VcfFileParser(@Nonnull File file) throws IOException {
		this(file.toPath());
	}

	public VcfFileParser(@Nonnull Path file) throws IOException {
		 this(() -> {
			try {
				return Files.lines(file);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	/**
	 * Constructs a new parser from a {@link Supplier} that creates a stream of strings.
	 * <strong>The supplier must return a fresh stream each call.</strong>
	 */
	public VcfFileParser(@Nonnull Supplier<Stream<String>> supplier) {
		m_supplier = supplier;
		m_metadata = new VcfMetadataParser().apply(supplier.get());
	}

	@Nonnull
	public VcfMetadataCollection getMetadata() {
		return m_metadata;
	}

	/**
	 * Reads the data lines.
	 */
	@Nonnull
	public Stream<VcfPosition> parse() throws IOException {
		return m_supplier.get().skip(m_lineNumber).map(new VcfDataParser());
	}

	@Nonnegative
	public long nLinesProcessed() {
		return m_lineNumber;
	}
}
