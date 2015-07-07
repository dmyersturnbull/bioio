package org.pharmgkb.parsers.pedigree;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Writes a {@link org.pharmgkb.parsers.pedigree.Pedigree} as a .ped file.
 * @author Douglas Myers-Turnbull
 */
public class PedigreeWriter implements Closeable {

	private PrintWriter m_writer;
	private String m_noParentMarker;
	private String m_fieldSeparator;
	private String m_femaleCode;
	private String m_maleCode;
	private String m_unknownCode;
	private Pedigree m_pedigree;

	private PedigreeWriter(@Nonnull PrintWriter writer, @Nonnull String noParentMarker, @Nonnull String fieldSeparator,
	                       @Nonnull String femaleCode, @Nonnull String maleCode, @Nonnull String unknownCode,
	                       @Nonnull Pedigree pedigree) {
		m_writer = writer;
		m_noParentMarker = noParentMarker;
		m_fieldSeparator = fieldSeparator;
		m_femaleCode = femaleCode;
		m_maleCode = maleCode;
		m_unknownCode = unknownCode;
		m_pedigree = pedigree;
	}

	public void write() {
		int i = 0;
		for (Family family : m_pedigree.getFamilies().values()) {
			Iterator<Individual> iter = family.topologicalOrder();
			while (iter.hasNext()) {
				Individual individual = iter.next();
				StringBuilder sb = new StringBuilder();
				sb.append(family.getId()).append(m_fieldSeparator);
				sb.append(individual.getId()).append(m_fieldSeparator);
				sb.append(!individual.getFather().isPresent()?
						          m_noParentMarker
						          : individual.getFather().get().getId()).append(m_fieldSeparator);
				sb.append(!individual.getMother().isPresent()?
						          m_noParentMarker
						          : individual.getMother().get().getId()).append(m_fieldSeparator);
				switch(individual.getSex()) {
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
				m_writer.println(sb);
				i++;
				if (i % 100 == 0) {
					m_writer.flush();
				}
			}
		}
	}

	@Override
	public void close() throws IOException {
		m_writer.close();
	}

	public static class Builder {

		private PrintWriter m_writer;
		private String m_noParentMarker = "0";
		private String m_fieldSeparator = "\t";
		private String m_femaleCodes = "2";
		private String m_maleCodes = "1";
		private String m_unknownCode = "3";
		private Pedigree m_pedigree;

		public Builder(@Nonnull File file, @Nonnull Pedigree pedigree) throws IOException {
			this(new PrintWriter(file), pedigree);
		}

		public Builder(@Nonnull PrintWriter writer, @Nonnull Pedigree pedigree) {
			m_writer = writer;
			m_pedigree = pedigree;
		}

		/**
		 * @param noParentMarker The marker that means the individual has no parents in the family; this is usually 0
		 */
		public void setNoParentMarker(@Nonnull String noParentMarker) {
			m_noParentMarker = noParentMarker;
		}

		/**
		 * @param fieldSeparator The string that separates columns; this is usually whitespace
		 */
		public void setFieldSeparator(@Nonnull String fieldSeparator) {
			m_fieldSeparator = fieldSeparator;
		}

		/**
		 * @param femaleCode The strings in the sex column that mean female; this is usually just 1
		 */
		public void setFemaleCodes(@Nonnull String femaleCode) {
			m_femaleCodes = femaleCode;
		}

		/**
		 * @param maleCode The strings in the sex column that mean male; this is usually just 0
		 */
		public void setMaleCodes(@Nonnull String maleCode) {
			m_maleCodes = maleCode;
		}

		/**
		 * @param unknownCode The strings in the sex column that mean unknown/other; this is set to 3 by default
		 */
		public void setUnknownCode(@Nonnull String unknownCode) {
			m_unknownCode = unknownCode;
		}

		@Nonnull
		public PedigreeWriter build() {
			return new PedigreeWriter(m_writer, m_noParentMarker, m_fieldSeparator, m_maleCodes, m_femaleCodes,
			                          m_unknownCode, m_pedigree);
		}
	}

}
