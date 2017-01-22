package org.pharmgkb.parsers.vcf.model.metadata;

import org.pharmgkb.parsers.vcf.utils.PropertyMapBuilder;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A ##SAMPLE metadata line.
 * @author Douglas Myers-Turnbull.
 */
public class VcfSampleMetadata extends VcfIdDescriptionMetadata {

	public static final String ID = "ID";
	public static final String DESCRIPTION = "Description";
	private static final long serialVersionUID = -4294361656055427579L;

	public VcfSampleMetadata(@Nonnull Map<String, String> props) {
		super(VcfMetadataType.Sample, props);
	}

	public VcfSampleMetadata(@Nonnull String id, @Nonnull String description) {
		super(VcfMetadataType.Sample, new PropertyMapBuilder().put(ID, id).put(DESCRIPTION, description).build());
	}

}
