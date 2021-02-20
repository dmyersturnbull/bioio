package org.pharmgkb.parsers.vcf.model.metadata;

import org.pharmgkb.parsers.vcf.utils.PropertyMapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.lang.invoke.MethodHandles;
import java.util.Map;


/**
 * Metadata for ##FORMAT lines.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfFormatMetadata extends VcfIdMetadata {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final String ID = "ID";
	public static final String DESCRIPTION = "Description";
	public static final String NUMBER = "Number";
	public static final String TYPE = "Type";

	private final VcfFormatNumber m_number;
	private final VcfFormatType m_type;

	public VcfFormatMetadata(@Nonnull Map<String, String> props) {
		super(VcfMetadataType.Format, props);
		super.require(ID, DESCRIPTION, NUMBER, TYPE);
		super.ensureNoExtras(ID, DESCRIPTION, NUMBER, TYPE);
		m_type = VcfFormatType.valueOf(props.get(TYPE));
		m_number = new VcfFormatNumber(props.get(NUMBER));
	}

	public VcfFormatMetadata(@Nonnull String id, long length, @Nonnull String description, @Nonnull String number,
							 @Nonnull VcfFormatType type) {
		this(new PropertyMapBuilder()
				.put(ID, id)
				.put(DESCRIPTION, description)
				.put(NUMBER, number)
				.put(TYPE, type.name())
				.build());
		// TODO: This isn't needed, right?
		//m_number = new VcfFormatNumber(number);
		//m_type = type;
	}

	@Nonnull
	public VcfFormatNumber getNumber() {
		return m_number;
	}

	@Nonnull
	public VcfFormatType getType() {
		return m_type;
	}
}
