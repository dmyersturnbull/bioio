package org.pharmgkb.parsers.pedigree.model;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * A collection of related individuals. Associated with an {@link #getId() Id} that is unique for this {@link Pedigree}.
 *
 * This class models the family as a DAG, where each node can have zero, one, or two parents, and any number of children.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class Family implements Subtree<Individual>, Serializable {

	private static final long serialVersionUID = 1305199874793758907L;

	private String m_id;

	private NavigableSet<Individual> m_roots;

	Family(@Nonnull String id) {
		m_id = id;
		m_roots = new TreeSet<>();
	}

	/**
	 * @return The Id of this Family
	 */
	@Nonnull
	public String getId() {
		return m_id;
	}

	/**
	 * An alias for {@link #inOrder()}.
	 */
	@Nonnull
	@Override
	public Iterator<Individual> iterator() {
		return inOrder();
	}

	/**
	 * Returns the {@link Individual} with the specified Id, or null if it doesn't exist.
	 */
	@Nonnull
	@Override
	public Optional<Individual> find(@Nonnull String individualId) {
		for (Individual root : m_roots) {
			Optional<Individual> found = root.find(individualId);
			if (found.isPresent()) {
				return found;
			}
		}
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Iterator<Individual> breadthFirst() {
		Set<Individual> visited = new LinkedHashSet<>();
		Queue<Individual> queue = new ArrayDeque<>(m_roots);
		while (!queue.isEmpty()) {
			Individual current = queue.poll();
			visited.add(current);
			queue.addAll(new ArrayList<>(current.getChildrenRaw()));
		}
		return visited.iterator();
	}

	@Nonnull
	@Override
	public Iterator<Individual> depthFirst() {
		Set<Individual> visited = new LinkedHashSet<>();
		for (Individual root : m_roots) {
			for (Individual child : root.getChildrenRaw()) {
				Iterator<Individual> i = child.depthFirst();
				while (i.hasNext()) {
					visited.add(i.next());
				}
			}
			visited.add(root);
		}
		return visited.iterator();
	}

	/**
	 * Returns the set of {@link Individual Individuals} with no parents.
	 */
	@Nonnull
	public NavigableSet<Individual> getRoots() {
		return new TreeSet<>(m_roots);
	}

	/**
	 * Returns an iterator that uses the lexigraphical ordering of the {@link Individual#getId() Individual Ids}.
	 * For example: a, b, c, d
	 */
	@Nonnull
	@Override
	public Iterator<Individual> inOrder() {
		Set<Individual> visited = new TreeSet<>();
		for (Individual root : m_roots) {
			Iterator<Individual> children = root.inOrder();
			while (children.hasNext()) {
				visited.add(children.next());
			}
		}
		return visited.iterator();
	}

	@Nonnull
	@Override
	public Iterator<Individual> topologicalOrder() {
		Set<Individual> fathersRemoved = new HashSet<>();
		Set<Individual> mothersRemoved = new HashSet<>();
		List<Individual> visited = new ArrayList<>();
		Queue<Individual> queue = new ArrayDeque<>(m_roots);
		for (Individual root : m_roots) {
			PedigreeUtils.computeTopologicalOrdering(visited, queue, fathersRemoved, mothersRemoved, root, m_id);
		}
		return visited.iterator();
	}

	/**
	 * For speed, only checks the individual Ids, which is safe since
	 * {@link PedigreeBuilder} ensures that no two Families share the same Id.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Family family = (Family) o;
		return m_id.equals(family.m_id);
	}

	@Override
	public int hashCode() {
		return m_id.hashCode();
	}

	@Override
	public String toString() {
		return "Family{" + "m_id='" + m_id + '\'' + '}';
	}

	void setId(@Nonnull String id) {
		m_id = id;
	}

	@Nonnull
	NavigableSet<Individual> getRootsRaw() {
		return m_roots;
	}
}
