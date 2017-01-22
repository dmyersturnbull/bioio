package org.pharmgkb.parsers.vcf.model.allele;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.vcf.utils.VcfEscapers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

/**
 * A VCF named allele, using the {@code <ID>} notation.
 * @author Douglas Myers-Turnbull
 */
public class VcfSymbolicAllele implements VcfAllele, Serializable {

	private static final long serialVersionUID = -7549822687429031748L;

	private final String m_id;

	@Nonnull
	public static VcfSymbolicAllele fromVcfAlt(@Nonnull String string) {
		Preconditions.checkNotNull(string, "Allele string cannot be null");
		Preconditions.checkArgument(string.startsWith("<") && string.endsWith(">"));
		return new VcfSymbolicAllele(
				VcfEscapers.SYMBOLIC_ALT_ID.unescape(string.substring(1, string.length() - 1))
		);
	}

	public VcfSymbolicAllele(@Nonnull String id) {
		Preconditions.checkNotNull(id, "Symbolic allele ID cannot be null");
		m_id = VcfEscapers.SYMBOLIC_ALT_ID.unescape(id);
	}

	@Override
	public boolean containsBase(@Nonnull char... bases) {
		return false; // impossible, save time
	}

	@Nonnull
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", m_id)
				.toString();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfSymbolicAllele that = (VcfSymbolicAllele) o;
		return Objects.equals(m_id, that.m_id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_id);
	}

	@Nonnull
	@Override
	public String toVcfString() {
		return "<" + VcfEscapers.SYMBOLIC_ALT_ID.escape(m_id) + ">";
	}
}
