package org.pharmgkb.parsers.vcf.model.metadata;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * Metadata for ##FILTER lines.
 * @author Douglas Myers-Turnbull.
 */
@Immutable
public class VcfFilterMetadata extends VcfIdDescriptionMetadata {

	public static final String ID = "ID";
	public static final String DESCRIPTION = "Description";

	public VcfFilterMetadata(@Nonnull Map<String, String> props) {
		super(VcfMetadataType.Filter, props);
	}

	public VcfFilterMetadata(@Nonnull String id, @Nonnull String description) {
		super(VcfMetadataType.Filter, id, description);
	}


}
