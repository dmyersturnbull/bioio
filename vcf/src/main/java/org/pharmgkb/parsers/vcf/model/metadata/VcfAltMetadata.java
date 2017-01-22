package org.pharmgkb.parsers.vcf.model.metadata;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * Metadata for ##ALT lines.
 * @author Douglas Myers-Turnbull.
 */
@Immutable
public class VcfAltMetadata extends VcfIdDescriptionMetadata {

	public static final String ID = "ID";
	public static final String DESCRIPTION = "Description";
	private static final long serialVersionUID = -856010915426592771L;

	public VcfAltMetadata(@Nonnull Map<String, String> props) {
		super(VcfMetadataType.Alt, props);
	}

	public VcfAltMetadata(@Nonnull String id, @Nonnull String description) {
		super(VcfMetadataType.Alt, id, description);
	}

}
