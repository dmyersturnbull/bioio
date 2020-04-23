package org.pharmgkb.parsers.pedigree.model;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A subtree of a {@link Pedigree}
 * @param <T> The nodes of the DAG; in this case, always {@link Individual}
 * @author Douglas Myers-Turnbull
 */
public interface Subtree<T> extends Iterable<T> {

	int SPLITERATOR_FLAGS = Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL;

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
	@Nonnull
	default Iterator<T> iterator() {
		return inOrder();
	}

	@Nonnull
	default Stream<T> breadthFirstStream() {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(breadthFirst(), SPLITERATOR_FLAGS),
				false
		);
	}

	@Nonnull
	default Stream<T> depthFirstStream() {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(depthFirst(), SPLITERATOR_FLAGS),
				false
		);
	}

	@Nonnull
	default Stream<T> inOrderStream() {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(inOrder(), SPLITERATOR_FLAGS),
				false
		);
	}

	@Nonnull
	default Stream<T> topologicalOrderStream() {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(topologicalOrder(), SPLITERATOR_FLAGS),
				false
		);
	}

	@Nonnull
	default Stream<T> stream() {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(iterator(), SPLITERATOR_FLAGS),
				false
		);
	}

	@Nonnull
	default Stream<T> parallelStream() {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(iterator(), SPLITERATOR_FLAGS),
				true
		);
	}


}
