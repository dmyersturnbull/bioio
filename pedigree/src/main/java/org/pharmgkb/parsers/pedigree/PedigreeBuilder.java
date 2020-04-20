package org.pharmgkb.parsers.pedigree;

import org.pharmgkb.parsers.ObjectBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A builder for {@link org.pharmgkb.parsers.pedigree.Pedigree Pedigrees}.
 *
 * For example:
 * {@code
 * PedigreeBuilder builder = new PedigreeBuilder();
 * builder.addIndividual("family1", "A", null, null, Sex.MALE, Arrays.asList("A/G", "disease"));
 * builder.addIndividual("family1", "B", null, null, Sex.FEMALE, Arrays.asList("T/C", "no_disease"));
 * builder.addIndividual("family1", "C", "A", "B", Sex.UNKNOWN, Arrays.asList("A/C", "disease"));
 * builder.addIndividual("family2", "D", null, null, Sex.MALE, Arrays.asList("A/T", "no_disease"));
 * builder.addIndividual("family2", "E", "D", null, Sex.FEMALE, Arrays.asList("A/T", "disease"));
 * }
 * This creates a two families:
 * <ul>
 *     <li>family1, a trio of A (father with disease), B (mother without disease), and C (child with disease)</li>
 *     <li>family2, a father-daughter duo of D (father without disease) and E (daughter with disease)</li>
 * </ul>
 *
 * @author Douglas Myers-Turnbull
 */
@NotThreadSafe
public class PedigreeBuilder implements ObjectBuilder<Pedigree> {

	private final boolean m_parentsAddedFirst;
	private Pedigree m_pedigree = new Pedigree();

	private Set<String> m_individualIdsUsed	= new HashSet<>();
	private Map<Individual, String> m_fatherPlaceholders = new HashMap<>();
	private Map<Individual, String> m_motherPlaceholders = new HashMap<>();

	/**
	 * @param parentsAddedFirst If true, construction is faster, but individuals must be added in order: if non-null, {@code fatherId} and {@code motherId}, must reference individuals that have already been added
	 */
	public PedigreeBuilder(boolean parentsAddedFirst) {
		m_parentsAddedFirst = parentsAddedFirst;
	}

	/**
	 *
	 * Adds an individual. If inOrder in the constructor is true, this individual's parents, if any, must already have been added.
	 *
	 * Note that the sex of a parent must be correct: if A is the mother of B, then A must have {@link org.pharmgkb.parsers.pedigree.Sex} FEMALE (cannot be MALE or UNKNOWN).
	 *
	 * @throws IllegalStateException If {@link #build()} was already called
	 *
	 * @throws java.lang.IllegalArgumentException If an Individual with the same {@code individualId} was already added,
	 *         or the mother or father (if non-null) does not exist or has the wrong Sex
	 */
	@Nonnull
	public PedigreeBuilder add(@Nonnull String familyId, @Nonnull String individualId,
	                                     @Nullable String fatherId, @Nullable String motherId,
	                                     @Nonnull Sex sex, @Nonnull List<String> info) {
		return addIndividual(familyId, individualId, Optional.ofNullable(fatherId), Optional.ofNullable(motherId),
		                     sex, info);
	}


	/**
	 *
	 * Adds an individual. If inOrder in the constructor is true, this individual's parents, if any, must already have been added.
	 *
	 * Note that the sex of a parent must be correct: if A is the mother of B, then A must have {@link org.pharmgkb.parsers.pedigree.Sex} FEMALE (cannot be MALE or UNKNOWN).
	 *
	 * @throws IllegalStateException If {@link #build()} was already called
	 *
	 * @throws java.lang.IllegalArgumentException If an Individual with the same {@code individualId} was already added,
	 *         or the mother or father (if non-null) does not exist or has the wrong Sex
	 */
	@Nonnull
	public PedigreeBuilder addIndividual(
			@Nonnull String familyId, @Nonnull String individualId,
	        @Nonnull Optional<String> fatherId, @Nonnull Optional<String> motherId,
			@Nonnull Sex sex, @Nonnull List<String> info
	) {

		// enforces immutability; see build()
		if (m_pedigree == null) {
			throw new IllegalStateException("PedigreeBuilder.build() already called");
		}

		if (m_individualIdsUsed.contains(individualId)) {
			throw new IllegalArgumentException("Duplicate Id for individual: " + individualId);
		}
		m_individualIdsUsed.add(individualId);

		Family family;
		if (m_pedigree.getFamiliesRaw().containsKey(familyId)) {
			family = m_pedigree.getFamily(familyId);
		} else {
			family = new Family(familyId);
			m_pedigree.getFamiliesRaw().put(familyId, family);
		}

		Individual individual = new Individual(individualId, sex, family, info);

		if (!fatherId.isPresent() && !motherId.isPresent()) {
			family.getRootsRaw().add(individual);
		} else {
			if (fatherId.isPresent()) {
				boolean fatherAttached = attachFather(individual, fatherId.get());
				if (!fatherAttached) {
					m_fatherPlaceholders.put(individual, fatherId.get());
				}
			}
			if (motherId.isPresent()) {
				boolean motherAttached = attachMother(individual, motherId.get());
				if (!motherAttached) {
					m_motherPlaceholders.put(individual, motherId.get());
				}
			}
		}

		if (!m_parentsAddedFirst) {
			attachRemaining(false);
		}

		return this;

	}

	/**
	 * Builds the {@link org.pharmgkb.parsers.pedigree.Pedigree}. After this method is called, calls to
	 * {@link #addIndividual(String, String, Optional, Optional, Sex, List)} will result in a
	 * {@link java.lang.IllegalStateException} being thrown.
	 */
	@Nonnull
	public Pedigree build() {
		attachRemaining(true);
		Pedigree pedigree = m_pedigree;
		m_pedigree = null; // ensure immutability of Pedigree
		return pedigree;
	}

	private boolean attachFather(@Nonnull Individual individual, @Nonnull String fatherId) {

		for (Individual test : individual.getFamily()) {

			if (fatherId.equals(test.getId())) {
				if (test.getSex() != Sex.MALE) {
					throw new IllegalArgumentException(
							"Individual " + individual.getId() + " must have a male father (Id: " + test.getId() + ")"
					);
				}
				test.getChildrenRaw().add(individual);
				individual.setFather(test);
				return true;
			}

		}

		return false;
	}

	private boolean attachMother(@Nonnull Individual individual, @Nonnull String motherId) {

		for (Individual test : individual.getFamily()) {

			if (motherId.equals(test.getId())) {
				if (test.getSex() != Sex.FEMALE) {
					throw new IllegalArgumentException(
							"Individual " +individual.getId()+ " must have a female mother (Id: " + test.getId() + ")");
				}
				test.getChildrenRaw().add(individual);
				individual.setMother(test);
				return true;
			}

		}

		return false;
	}


	private void attachRemaining(boolean require) {

		for (Map.Entry<Individual, String> entry : m_fatherPlaceholders.entrySet()) {
			Individual individual = entry.getKey();
			String fatherId = entry.getValue();
			boolean attached = attachFather(individual, fatherId);
			if (require && !attached) {
				throw new IllegalArgumentException(
						"Father of individual " + individual.getId()
						+ " with father "+ fatherId + " does not exist in family "
						+ individual.getFamily().getId()
				);
			}
		}

		for (Map.Entry<Individual, String> entry : m_motherPlaceholders.entrySet()) {
			Individual individual = entry.getKey();
			String motherId = entry.getValue();
			boolean attached = attachMother(individual, motherId);
			if (require && !attached) {
				throw new IllegalArgumentException(
						"Mother of individual " + individual.getId() + " with mother "
						+ motherId + " does not exist in family "
						+ individual.getFamily().getId()
				);
			}
		}
	}

}
