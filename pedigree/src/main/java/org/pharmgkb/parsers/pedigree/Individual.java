package org.pharmgkb.parsers.pedigree;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import java.util.stream.Collectors;

/**
 * A member of a {@link org.pharmgkb.parsers.pedigree.Family}.
 * Associated with an {@link #getId() Id} that is unique for this {@link org.pharmgkb.parsers.pedigree.Pedigree}.
 *
 * Implements {@link org.pharmgkb.parsers.pedigree.Subtree} for search and traversals using <em>this node</em> as root.
 * For example, if A is a child of B, and C is a child of B, calling B.find("C") will return C,but calling B.find("A")
 * will return null.
 * Similarly, the iterator B.breadthFirst() will contain (B, C); it will not contain A.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class Individual implements Subtree<Individual>, Comparable<Individual>, Serializable {

	private static final long serialVersionUID = 6429782113364423410L;

	private String m_id;

	private Family m_family;

	private Optional<Individual> m_father = Optional.empty();

	private Optional<Individual> m_mother = Optional.empty();

	private NavigableSet<Individual> m_children;

	private Sex m_sex;

	private List<String> m_info;

	Individual(@Nonnull String id, @Nonnull Sex sex, @Nonnull Family family, @Nonnull List<String> info) {
		m_id = id;
		m_sex = sex;
		m_family = family;
		m_info = info;
		m_children = new TreeSet<>();
	}

	@Nonnull
	@Override
	public Optional<Individual> find(@Nonnull String individualId) {
		for (Individual node : this) {
			if (node.getId().equals(individualId)) {
				return Optional.of(node);
			}
		}
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Iterator<Individual> breadthFirst() {
		List<Individual> visited = new ArrayList<>();
		Queue<Individual> queue = new ArrayDeque<>();
		queue.add(this);
		while (!queue.isEmpty()) {
			Individual current = queue.poll();
			visited.add(current);
			queue.addAll(current.getChildrenRaw().stream().collect(Collectors.toList()));
		}
		return visited.iterator();
	}

	@Nonnull
	@Override
	public Iterator<Individual> depthFirst() {
		Set<Individual> visited = new LinkedHashSet<>();
		for (Individual child : m_children) {
			Iterator<Individual> i = child.depthFirst();
			while (i.hasNext()) {
				visited.add(i.next());
			}
		}
		visited.add(this);
		return visited.iterator();
	}

	@Nonnull
	@Override
	public Iterator<Individual> inOrder() {
		List<Individual> visited = new ArrayList<>();
		visited.add(this);
		for (Individual child : m_children) {
			Iterator<Individual> i = child.inOrder();
			while (i.hasNext()) {
				visited.add(i.next());
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
		Queue<Individual> queue = new ArrayDeque<>();
		queue.add(this);
		PedigreeUtils.computeTopologicalOrdering(visited, queue, fathersRemoved, mothersRemoved, this, m_id);
		return visited.iterator();
	}

	@Nonnull
	@Override
	public Iterator<Individual> iterator() {
		return inOrder();
	}

	/**
	 * Additional annotations, such as genotype, phenotype, probrand status, and diseases state.
	 */
	@Nonnull
	public List<String> getInfo() {
		return new ArrayList<>(m_info);
	}

	@Nonnull
	public String getId() {
		return m_id;
	}

	@Nonnull
	public Family getFamily() {
		return m_family;
	}

	@Override
	public int compareTo(Individual other) {
		return m_id.compareTo(other.getId());
	}

	void setId(String id) {
		m_id = id;
	}

	@Nonnull
	public Optional<Individual> getFather() {
		return m_father;
	}
	void setFather(@Nullable Individual test) {
		setFather(Optional.ofNullable(test));
	}
	void setFather(@Nonnull Optional<Individual> father) {
		m_father = father;
	}

	@Nonnull
	public Optional<Individual> getMother() {
		return m_mother;
	}
	void setMother(@Nullable Individual test) {
		setMother(Optional.ofNullable(test));
	}
	void setMother(@Nonnull Optional<Individual> mother) {
		m_mother = mother;
	}

	@Nonnull
	public Sex getSex() {
		return m_sex;
	}

	void setSex(Sex sex) {
		m_sex = sex;
	}

	@Nonnull
	public NavigableSet<Individual> getChildren() {
		return new TreeSet<>(m_children);
	}

	void setChildren(NavigableSet<Individual> children) {
		m_children = children;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Individual that = (Individual) o;
		return m_id.equals(that.m_id) && m_family.equals(that.m_family);
	}

	@Override
	public int hashCode() {
		return m_id.hashCode();
	}

	public String toSimpleString() {
		return "Individual{" + "m_id='" + m_id + '\'' + '}';
	}

	@Override
	public String toString() {
		return "Individual{" +
				"id='" + m_id + '\'' +
				", family=" + m_family +
				", father=" + m_father +
				", mother=" + m_mother +
				", children=" + m_children +
				", sex=" + m_sex +
				", info=" + m_info +
				'}';
	}

	@SuppressWarnings("SuspiciousGetterSetter")
	NavigableSet<Individual> getChildrenRaw() {
		return m_children;
	}
}
