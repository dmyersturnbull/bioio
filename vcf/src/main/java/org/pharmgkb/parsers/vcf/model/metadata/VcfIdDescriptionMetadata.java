package org.pharmgkb.parsers.vcf.model.metadata;

import org.pharmgkb.parsers.vcf.utils.PropertyMapBuilder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * A VCF metadata line that contains an ID and a description.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public abstract class VcfIdDescriptionMetadata extends VcfIdMetadata {

	public static final String ID = "ID";
	public static final String DESCRIPTION = "Description";
	private static final long serialVersionUID = 1069963979940331972L;

	public VcfIdDescriptionMetadata(@Nonnull  VcfMetadataType type, @Nonnull Map<String, String> props) {
		super(type, props);
		super.require(ID, DESCRIPTION);
		super.ensureNoExtras(ID, DESCRIPTION);
	}

	public VcfIdDescriptionMetadata(@Nonnull VcfMetadataType type, @Nonnull String id, @Nonnull String description) {
		this(type, new PropertyMapBuilder().put(ID, id).put(DESCRIPTION, description).build());
	}

	@Nonnull
	public String getDescription() {
		//noinspection OptionalGetWithoutIsPresent
		return getPropertyRaw(DESCRIPTION).get();
	}

}
