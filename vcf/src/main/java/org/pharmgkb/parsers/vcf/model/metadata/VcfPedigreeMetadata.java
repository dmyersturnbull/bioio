package org.pharmgkb.parsers.vcf.model.metadata;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * A ##PEDIGREE metadata line.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfPedigreeMetadata extends VcfIdMetadata {

	public VcfPedigreeMetadata(@Nonnull Map<String, String> properties) {
		super(VcfMetadataType.Pedigree, properties);
		super.require(ID);
	}
}
