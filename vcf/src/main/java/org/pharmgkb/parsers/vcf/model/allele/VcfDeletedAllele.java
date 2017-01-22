package org.pharmgkb.parsers.vcf.model.allele;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * The unique allele {@code *}, indicating that the allele cannot exist due to an <em>upstream</em> deletion.
 * @author Douglas Myers-Turnbull
 */
public class VcfDeletedAllele implements VcfAllele, Serializable {

	public static final VcfDeletedAllele DELETED = new VcfDeletedAllele();

	private VcfDeletedAllele() {
	}

	@Nonnull
	@Override
	public String toVcfString() {
		return "*";
	}

	@Nonnull
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).toString();
	}

	@Override
	public boolean equals(Object o) {
		return this == o || !(o == null || getClass() != o.getClass());
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
