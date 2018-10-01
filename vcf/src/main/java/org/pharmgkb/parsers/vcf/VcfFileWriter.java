package org.pharmgkb.parsers.vcf;

import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.vcf.model.VcfMetadataCollection;
import org.pharmgkb.parsers.vcf.model.VcfPosition;
import org.pharmgkb.parsers.vcf.model.metadata.VcfMetadata;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * A convenience class for writing both metadata and VCF positions to a file.
 * @author Douglas Myers-Turnbull
 */
@NotThreadSafe
public class VcfFileWriter implements Closeable {

	private final PrintWriter m_writer;
	private final int m_flushEvery;

	@Nonnull
	public static Stream<String> concat(@Nonnull VcfMetadataCollection metadata, @Nonnull Stream<VcfPosition> positions) {
		return Stream.concat(
				metadata.getLines().stream().map(new VcfMetadataWriter()),
				positions.map(new VcfDataWriter())
		);
	}

	private VcfFileWriter(@Nonnull Builder builder) {
		m_writer = builder.m_writer;
		m_flushEvery = builder.m_flushEvery;
	}

	public void write(@Nonnull VcfMetadataCollection metadata, @Nonnull Stream<VcfPosition> positions) throws IOException {
		Preconditions.checkNotNull(positions, "Positions cannot be null");
		Preconditions.checkNotNull(metadata, "Metadata cannot be null");
		write(metadata.getLines().stream(), positions);
	}

	public void write(@Nonnull Stream<VcfMetadata> metadata, @Nonnull Stream<VcfPosition> positions) throws IOException {
		Preconditions.checkNotNull(metadata, "Metadata cannot be null");
		Preconditions.checkNotNull(positions, "Positions cannot be null");
		metadata.map(new VcfMetadataWriter())
				.forEach(m_writer::println);
		m_writer.flush();
		VcfDataWriter writer = new VcfDataWriter();
		positions.forEach(p -> {
			m_writer.println(writer.apply(p));
			if (writer.nLinesProcessed() % m_flushEvery == 0) {
				m_writer.flush();
			}
		});
	}

	@Override
	public void close() throws IOException {
		m_writer.close();
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<VcfFileWriter> {

		private final PrintWriter m_writer;
		private int m_flushEvery = 10000;

		public Builder(@Nonnull PrintWriter writer) {
			Preconditions.checkNotNull(writer, "Writer cannot be null");
			m_writer = writer;
		}

		public Builder(@Nonnull File file) throws IOException {
			Preconditions.checkNotNull(file, "File cannot be null");
			m_writer = new PrintWriter(new FileWriter(file));
		}

		public Builder(@Nonnull Path file) throws IOException {
			Preconditions.checkNotNull(file, "File cannot be null");
			m_writer = new PrintWriter(new FileWriter(file.toFile()));
		}

		/**
		 * @param flushEvery 0 means flush only when finished
		 */
		public Builder setFlushEvery(@Nonnegative int flushEvery) {
			Preconditions.checkArgument(flushEvery > -1, "Flush frequency must be at least 0");
			m_flushEvery = flushEvery;
			return this;
		}

		@Nonnull
		@Override
		public VcfFileWriter build() {
			return new VcfFileWriter(this);
		}
	}

	@Override
	public String toString() {
		return "VcfFileWriter{" +
				"writer=" + m_writer +
				", flushEvery=" + m_flushEvery +
				'}';
	}
}
