package org.pharmgkb.parsers.pedigree.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Package-level utilities for building and traversing {@link Pedigree Pedigrees}.
 * @author Douglas Myers-Turnbull
 */
class PedigreeUtils {

	/**
	 * Add the roots first!
	 */
	static void computeTopologicalOrdering(
			@Nonnull List<? super Individual> visited, @Nonnull Queue<Individual> queue,
			@Nonnull Set<? super Individual> fathersRemoved, @Nonnull Set<? super Individual> mothersRemoved,
			@Nonnull Individual root, @Nonnull String id
	) {
		while (!queue.isEmpty()) {
			Individual current = queue.poll();
			visited.add(current);
			for (Individual child : current.getChildren()) {
				// assume that father iff male and mother iff female
				// we check for this in the constructor and in PedigreeBuilder
				if (current.getSex() == Sex.MALE) {
					fathersRemoved.add(child);
				} else {
					mothersRemoved.add(child);
				}
				if (
						(child.getFather().isEmpty() || fathersRemoved.contains(child))
						&& (child.getMother().isEmpty() || mothersRemoved.contains(child))
				) {
					// Because PedigreeBuilder only adds edges when it adds nodes, this is impossible
					// Keep it here in case we add an alternate way to build pedigrees
					if (queue.contains(child)) {
						throw new IllegalStateException(
								"Pedigree for " + id + " contains a cycle! See edge "
								+ current.getId() + " --> " + child.getId()
						);
					}
					queue.add(child);
				}
			}
		}
	}

}
