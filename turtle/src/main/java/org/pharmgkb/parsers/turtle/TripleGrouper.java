package org.pharmgkb.parsers.turtle;

import com.google.common.collect.ImmutableMap;
import org.pharmgkb.parsers.turtle.model.Node;
import org.pharmgkb.parsers.turtle.model.Triple;
import org.pharmgkb.parsers.turtle.model.TripleGroup;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * The idea here is to flatMap a Stream&lt;Triple&gt;.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class TripleGrouper implements Function<Triple, Stream<TripleGroup>> {

	private String m_previousSubject = null;
	private String m_subject = null;
	private Map<String, Node> m_list = new HashMap<>();

	@Nonnull
	public Stream<TripleGroup> apply(@Nonnull Triple triple) {
			if (!triple.getSubject().getValue().equals(m_subject)) {
				m_subject = triple.getSubject().getValue();
					if (!m_list.isEmpty()) {
							return Stream.of(new TripleGroup(m_previousSubject, ImmutableMap.copyOf(m_list)));
					}
				m_previousSubject = m_subject;
			}
		m_list.put(triple.getPredicate().getValue(), triple.getObject());
			return Stream.empty();
	}

	@Override
	public String toString() {
		return "TripleGrouper{" +
				"previousSubject='" + m_previousSubject + '\'' +
				", subject='" + m_subject + '\'' +
				", list=" + m_list +
				'}';
	}

	@Nonnull
	public Stream<TripleGroup> convert(@Nonnull Stream<Triple> triples) {
		return triples.flatMap(this);
	}
}
