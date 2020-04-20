package org.pharmgkb.parsers.genbank;

import com.google.common.base.Enums;
import com.google.common.base.MoreObjects;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class LocusAnnotation implements GenbankAnnotation {

	private String m_locusName;
	private String m_sequenceLength;
	private String m_moleculeType;
	private String m_division;
	private LocalDate m_modificationDate;
	private GenbankDivision m_standardDivision;

	public LocusAnnotation(
			String locusName,
			String sequenceLength,
			String moleculeType,
			String division,
			LocalDate modificationDate
	) {
		this.m_locusName = locusName;
		this.m_sequenceLength = sequenceLength;
		this.m_moleculeType = moleculeType;
		this.m_division = division;
		this.m_modificationDate = modificationDate;
		this.m_standardDivision = Enums.getIfPresent(GenbankDivision.class, division).or(GenbankDivision.NONSTANDARD);
	}

	@Nonnull
	public String getLocusName() {
		return m_locusName;
	}

	@Nonnull
	@Nonnegative
	public String getSequenceLength() {
		return m_sequenceLength;
	}

	@Nonnull
	public String getMoleculeType() {
		return m_moleculeType;
	}

	@Nonnull
	public String getDivision() {
		return m_division;
	}

	@Nonnull
	public LocalDate getModificationDate() {
		return m_modificationDate;
	}

	@Nonnull
	public GenbankDivision getStandardDivision() {
		return m_standardDivision;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LocusAnnotation that = (LocusAnnotation) o;
		return Objects.equals(m_locusName, that.m_locusName) &&
				Objects.equals(m_sequenceLength, that.m_sequenceLength) &&
				Objects.equals(m_moleculeType, that.m_moleculeType) &&
				Objects.equals(m_division, that.m_division) &&
				Objects.equals(m_modificationDate, that.m_modificationDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_locusName, m_sequenceLength, m_moleculeType, m_division, m_modificationDate);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("locusName", m_locusName)
				.add("sequenceLength", m_sequenceLength)
				.add("moleculeType", m_moleculeType)
				.add("genbankDivision", m_division)
				.add("modificationDate", m_modificationDate)
				.toString();
	}
}
