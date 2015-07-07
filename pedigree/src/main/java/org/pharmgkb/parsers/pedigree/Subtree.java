package org.pharmgkb.parsers.pedigree;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Optional;

/**
 * A subtree of a {@link org.pharmgkb.parsers.pedigree.Pedigree}
 * @param <T> The nodes of the DAG; in this case, always {@link org.pharmgkb.parsers.pedigree.Individual}
 * @author Douglas Myers-Turnbull
 */
public interface Subtree<T> extends Iterable<T> {

	@Nonnull
	Optional<T> find(@Nonnull String individualId);

	@Nonnull
	Iterator<T> breadthFirst();

	@Nonnull
	Iterator<T> depthFirst();

	@Nonnull
	Iterator<T> inOrder();

	@Nonnull
	Iterator<T> topologicalOrder();

	@Override
	default Iterator<T> iterator() {
		return inOrder();
	}


}
