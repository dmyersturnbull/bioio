package org.pharmgkb.parsers.vcf.model.metadata;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.pharmgkb.parsers.vcf.utils.VcfEscapers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The only VCF metadata line that contains just a single {@code #}, which is required to be immediately prior to the
 * first position line, if any.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfHeaderMetadata implements VcfMetadata {

	private static final long serialVersionUID = 6641034538235680113L;
	private ImmutableList<String> m_sampleNames;

	public VcfHeaderMetadata(@Nonnull List<String> sampleNames) {
		Preconditions.checkNotNull(sampleNames, "List of sample names can be empty but not null");
		m_sampleNames = ImmutableList.copyOf(sampleNames);
	}

	@Nonnull
	public ImmutableList<String> getSampleNames() {
		return m_sampleNames;
	}

	@Nonnull
	@Override
	public String toVcfLine() {
		// do it this way to make sure we don't have a trailing \t
		return "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT"
				+ m_sampleNames.stream().map(s -> "\t" + VcfEscapers.SAMPLE_NAME.escape(s))
				.collect(Collectors.joining());
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfHeaderMetadata that = (VcfHeaderMetadata) o;
		return Objects.equal(m_sampleNames, that.m_sampleNames);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(m_sampleNames);
	}

	@Override
	@Nonnull
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("sampleNames", "[" + String.join(",", m_sampleNames) + "]")
				.toString();
	}
}
