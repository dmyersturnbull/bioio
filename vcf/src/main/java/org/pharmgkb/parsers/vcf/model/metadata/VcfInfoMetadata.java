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
	private static final long serialVersionUID = 8959638735279037977L;

	private VcfInfoType m_type;

	private VcfFormatNumber m_number;

	public VcfInfoMetadata(@Nonnull Map<String, String> props) {
		super(VcfMetadataType.Info, props);
		super.require(ID, DESCRIPTION, NUMBER, TYPE);
		super.ensureNoExtras(ID, DESCRIPTION, NUMBER, TYPE, SOURCE, VERSION);
		m_type = VcfInfoType.valueOf(props.get(TYPE));
		m_number = new VcfFormatNumber(props.get(NUMBER));
	}

	public VcfInfoMetadata(@Nonnull String id, @Nonnull String description, @Nonnull String number, @Nonnull VcfInfoType type,
						   @Nullable String source, @Nullable String version) {
		this(new PropertyMapBuilder()
				.put(ID, id)
				.put(DESCRIPTION, description)
				.put(NUMBER, number)
				.put(TYPE, type.name())
				.put(SOURCE, source)
				.put(VERSION, version)
				.build());
		m_number = new VcfFormatNumber(number);
		m_type = type;
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

		private final String id;
		private final String description;
		private final String number;
		private final VcfInfoType type;

		private String source = null;
		private String version = null;

		public Builder(@Nonnull String id, @Nonnull String description, @Nonnull String number, @Nonnull VcfInfoType type) {
			this.id = id;
			this.description = description;
			this.number = number;
			this.type = type;
		}

		public Builder setSource(@Nullable String source) {
			this.source = source;
			return this;
		}

		public Builder setVersion(@Nullable String version) {
			this.version = version;
			return this;
		}

		@Nonnull
		@Override
		public VcfInfoMetadata build() {
			return new VcfInfoMetadata(id, description, number, type, source, version);
		}
	}

}
