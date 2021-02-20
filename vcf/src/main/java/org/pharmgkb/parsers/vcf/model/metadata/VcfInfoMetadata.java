package org.pharmgkb.parsers.vcf.model.metadata;

import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.vcf.utils.PropertyMapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.Map;


/**
 * A VCF ##INFO metadata line.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfInfoMetadata extends VcfIdMetadata {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final String ID = "ID";
	public static final String DESCRIPTION = "Description"; // should be quoted
	public static final String NUMBER = "Number";
	public static final String TYPE = "Type";
	public static final String SOURCE = "Source"; // should be quoted
	public static final String VERSION = "Version"; // should be quoted

	private final VcfInfoType m_type;

	private final VcfFormatNumber m_number;

	public VcfInfoMetadata(@Nonnull Map<String, String> props) {
		super(VcfMetadataType.Info, props);
		super.require(ID, DESCRIPTION, NUMBER, TYPE);
		super.ensureNoExtras(ID, DESCRIPTION, NUMBER, TYPE, SOURCE, VERSION);
		m_type = VcfInfoType.valueOf(props.get(TYPE));
		m_number = new VcfFormatNumber(props.get(NUMBER));
	}

	public VcfInfoMetadata(
			@Nonnull String id, @Nonnull String description,
			@Nonnull String number, @Nonnull VcfInfoType type,
			@Nullable String source, @Nullable String version
	) {
		this(new PropertyMapBuilder()
				.put(ID, id)
				.put(DESCRIPTION, description)
				.put(NUMBER, number)
				.put(TYPE, type.name())
				.put(SOURCE, source)
				.put(VERSION, version)
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
	public VcfInfoType getType() {
		return m_type;
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<VcfInfoMetadata> {

		private final String m_id;
		private final String m_description;
		private final String m_number;
		private final VcfInfoType m_type;

		private String m_source = null;
		private String m_version = null;

		public Builder(@Nonnull String id, @Nonnull String description, @Nonnull String number, @Nonnull VcfInfoType type) {
			m_id = id;
			m_description = description;
			m_number = number;
			m_type = type;
		}

		public Builder setSource(@Nullable String source) {
			m_source = source;
			return this;
		}

		public Builder setVersion(@Nullable String version) {
			m_version = version;
			return this;
		}

		@Nonnull
		@Override
		public VcfInfoMetadata build() {
			return new VcfInfoMetadata(m_id, m_description, m_number, m_type, m_source, m_version);
		}
	}

	@Override
	public String toString() {
		return "VcfInfoMetadata{" +
				"type=" + m_type +
				", number=" + m_number +
				'}';
	}
}
