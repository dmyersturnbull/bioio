package org.pharmgkb.parsers.vcf.model.metadata;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.vcf.utils.VcfEscapers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * A VCF metadata line that is either not mentioned in the specification or has no defined structure.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfRawMetadata implements VcfMetadata {

	private static final long serialVersionUID = 4838948408864511011L;
	private String m_line;

	public VcfRawMetadata(@Nonnull String line) {
		Preconditions.checkNotNull(line, "Metadata line cannot be null");
		m_line = VcfEscapers.METADATA.unescape(line);
	}

	@Nonnull
	@Override
	public String toVcfLine() {
		return VcfEscapers.METADATA.escape(m_line);
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfRawMetadata that = (VcfRawMetadata) o;
		return Objects.equal(m_line, that.m_line);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(m_line);
	}

	@Override
	@Nonnull
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("line", m_line)
				.toString();
	}
}
