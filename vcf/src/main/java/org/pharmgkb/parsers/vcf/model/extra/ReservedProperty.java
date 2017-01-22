package org.pharmgkb.parsers.vcf.model.extra;

import javax.annotation.Nonnull;

/**
 * A field specified as reserved in the VCF specification.
 * @author Douglas Myers-Turnbull
 */
public interface ReservedProperty {

	@Nonnull
	String getId();

	@Nonnull
	String getDescription();

	@Nonnull
	Class<?> getType();

	boolean isList();
}
