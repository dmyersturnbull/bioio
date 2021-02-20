package org.pharmgkb.parsers.vcf.model.metadata;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * A VCF metadata line that contains an ID.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public abstract class VcfIdMetadata extends VcfMapMetadata {

	public static final String ID = "ID";

	public VcfIdMetadata(@Nonnull VcfMetadataType type, @Nonnull Map<String, String> properties) {
		super(type, properties);
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Nonnull
	public String getId() {
		return getPropertyRaw(ID).get();
	}

}
