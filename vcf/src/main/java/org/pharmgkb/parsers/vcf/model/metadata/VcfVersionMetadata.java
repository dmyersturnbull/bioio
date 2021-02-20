package org.pharmgkb.parsers.vcf.model.metadata;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * The first metadata line in a VCF file, starting with {@code ##fileFormat=VCFv}.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfVersionMetadata implements VcfMetadata {

	private final String m_versionNumber;

	public VcfVersionMetadata(@Nonnull String versionNumber) {
		Preconditions.checkNotNull(versionNumber, "Version number cannot be null");
		m_versionNumber = versionNumber;
	}

	@Nonnull
	@Override
	public String toVcfLine() {
		return "##fileformat=VCFv" + m_versionNumber;
	}

	@Nonnull
	public String getVersionNumber() {
		return m_versionNumber;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfVersionMetadata that = (VcfVersionMetadata) o;
		return Objects.equal(m_versionNumber, that.m_versionNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(m_versionNumber);
	}

	@Override
	@Nonnull
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("versionNumber", m_versionNumber)
				.toString();
	}
}
