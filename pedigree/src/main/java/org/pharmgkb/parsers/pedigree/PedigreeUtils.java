package org.pharmgkb.parsers.pedigree;

import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Package-level utilities for building and traversing {@link org.pharmgkb.parsers.pedigree.Pedigree Pedigrees}.
 * @author Douglas Myers-Turnbull
 */
class PedigreeUtils {

	/**
	 * Add the roots first!
	 */
	static void computeTopologicalOrdering(List<Individual> visited, Queue<Individual> queue,
	                                       Set<Individual> fathersRemoved, Set<Individual> mothersRemoved,
	                                       Individual root, String id) {
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
				if ((!child.getFather().isPresent()|| fathersRemoved.contains(child))
						&& (!child.getMother().isPresent() || mothersRemoved.contains(child))) {
					// Because PedigreeBuilder only adds edges when it adds nodes, this is impossible
					// Keep it here in case we add an alternate way to build pedigrees
					if (queue.contains(child)) {
						throw new IllegalStateException("Pedigree for " + id + " contains a cycle! See edge "
								                                + current.getId() + " --> " + child.getId());
					}
					queue.add(child);
				}
			}
		}
	}

}
