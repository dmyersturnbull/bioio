package org.pharmgkb.parsers.pedigree;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * An immutable pedigree containing a set of {@link org.pharmgkb.parsers.pedigree.Family Families}.
 * Individuals can only be related to individuals in the same family.
 * @see org.pharmgkb.parsers.pedigree.PedigreeBuilder For building a pedigree programmatically
 * @see org.pharmgkb.parsers.pedigree.PedigreeParser For building a pedigree from a .ped/LINKAGE/QTDT format
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class Pedigree implements Iterable<Family> {

	private NavigableMap<String, Family> m_families;

	Pedigree() {
		m_families = new TreeMap<>();
	}

	public NavigableMap<String, Family> getFamilies() {
		return new TreeMap<>(m_families);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Pedigree pedigree = (Pedigree) o;
		return m_families.equals(pedigree.m_families);
	}

	@Override
	public int hashCode() {
		return m_families.hashCode();
	}

	@Nonnull
	public Family getFamily(@Nonnull String familyId) {
		return m_families.get(familyId);
	}

	@Override
	public Iterator<Family> iterator() {
		return m_families.values().iterator();
	}

	@SuppressWarnings("SuspiciousGetterSetter")
	NavigableMap<String, Family> getFamiliesRaw() {
		return m_families;
	}
}
