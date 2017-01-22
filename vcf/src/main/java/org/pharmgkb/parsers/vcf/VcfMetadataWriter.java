package org.pharmgkb.parsers.vcf;

import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.LineWriter;
import org.pharmgkb.parsers.vcf.model.VcfMetadataCollection;
import org.pharmgkb.parsers.vcf.model.metadata.VcfMetadata;
import org.pharmgkb.parsers.vcf.utils.VcfEscapers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

/**
 *
 * Writes VCF metadata line-by-line. Simply calls {@link VcfMetadata#toVcfLine()} for each metadata line.
 * A metadata line is any line that begins with a {@code #}, including the {@code vcfVersion line} and the header line.
 *
 * For example, using {@link VcfMetadataCollection}:
 * <code>
 *     new VcfMetadataWriter().writeToFile(metadataCollection.getLines(), outputFile);
 * </code>
 *
 * This writer is <strong>not thread-safe</strong>. This is because certain metadata lines need to be written in a
 * particular order to follow the VCF specification.
 *
 * @author Douglas Myers-Turnbull
 */
@NotThreadSafe
public class VcfMetadataWriter implements LineWriter<VcfMetadata> {

	private static final long sf_logEvery = 10000;

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private long m_lineNumber = 0l;

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber;
	}

	@Nonnull
	public Stream<String> apply(@Nonnull VcfMetadataCollection collection) {
		Preconditions.checkNotNull(collection, "Metadata cannot be null");
		return collection.getLines().stream().map(this);
	}

	@Nonnull
	@Override
	public String apply(@Nonnull VcfMetadata vcfMetadata) {
		Preconditions.checkNotNull(vcfMetadata, "Metadata cannot be null");
		m_lineNumber++;
		return vcfMetadata.toVcfLine();
	}

}
