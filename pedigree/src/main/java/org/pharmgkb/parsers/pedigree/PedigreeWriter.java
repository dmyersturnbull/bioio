package org.pharmgkb.parsers.pedigree;

import org.pharmgkb.parsers.LineStructureWriter;
import org.pharmgkb.parsers.ObjectBuilder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.stream.Stream;

/**
 * Writes a {@link org.pharmgkb.parsers.pedigree.Pedigree} as a .ped file.
 * @author Douglas Myers-Turnbull
 */
public class PedigreeWriter implements LineStructureWriter<Pedigree> {

	private String m_noParentMarker;
	private String m_fieldSeparator;
	private String m_femaleCode;
	private String m_maleCode;
	private String m_unknownCode;

	private PedigreeWriter(@Nonnull Builder builder) {
		m_noParentMarker = builder.m_noParentMarker;
		m_fieldSeparator = builder.m_fieldSeparator;
		m_femaleCode = builder.m_femaleCode;
		m_maleCode = builder.m_maleCode;
		m_unknownCode = builder.m_unknownCode;
	}

	@Override
	@Nonnull
	public Stream<String> apply(@Nonnull Pedigree pedigree) {

		return pedigree.getFamilies().values().parallelStream()
				.flatMap(family -> family.topologicalOrderStream()
						.map(individual -> {
							StringBuilder sb = new StringBuilder();
							sb.append(family.getId()).append(m_fieldSeparator);
							sb.append(individual.getId()).append(m_fieldSeparator);
							sb.append(!individual.getFather().isPresent() ? m_noParentMarker
									          : individual.getFather().get().getId()).append(m_fieldSeparator);
							sb.append(!individual.getMother().isPresent() ? m_noParentMarker
									          : individual.getMother().get().getId()).append(m_fieldSeparator);
							switch (individual.getSex()) {
								case MALE:
									sb.append(m_maleCode);
									break;
								case FEMALE:
									sb.append(m_femaleCode);
									break;
								case UNKNOWN:
									sb.append(m_femaleCode);
									break;
							}
							for (String info : individual.getInfo()) {
								sb.append(m_fieldSeparator).append(info);
							}
							return sb.toString();
						}));

	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<PedigreeWriter> {

		private String m_noParentMarker = "0";
		private String m_fieldSeparator = "\t";
		private String m_femaleCode = "2";
		private String m_maleCode = "1";
		private String m_unknownCode = "3";

		/**
		 * @param noParentMarker The marker that means the individual has no parents in the family; this is usually 0
		 */
		@Nonnull
		public Builder setNoParentMarker(@Nonnull String noParentMarker) {
			m_noParentMarker = noParentMarker;
			return this;
		}

		/**
		 * @param fieldSeparator The string that separates columns; this is usually whitespace
		 */
		@Nonnull
		public Builder setFieldSeparator(@Nonnull String fieldSeparator) {
			m_fieldSeparator = fieldSeparator;
			return this;
		}

		/**
		 * @param femaleCode The strings in the sex column that mean female; this is usually just 1
		 */
		@Nonnull
		public Builder setFemaleCode(@Nonnull String femaleCode) {
			m_femaleCode = femaleCode;
			return this;
		}

		/**
		 * @param maleCode The strings in the sex column that mean male; this is usually just 0
		 */
		@Nonnull
		public Builder setMaleCode(@Nonnull String maleCode) {
			m_maleCode = maleCode;
			return this;
		}

		/**
		 * @param unknownCode The strings in the sex column that mean unknown/other; this is set to 3 by default
		 */
		@Nonnull
		public Builder setUnknownCode(@Nonnull String unknownCode) {
			m_unknownCode = unknownCode;
			return this;
		}

		@Nonnull
		public PedigreeWriter build() {
			return new PedigreeWriter(this);
		}
	}

}
