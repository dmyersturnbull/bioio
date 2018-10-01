package org.pharmgkb.parsers.turtle;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


/**
 * The idea here is to flatMap a Stream&lt;Triple&gt;.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class TripleGrouper {

	private String m_previousSubject = null;
	private String m_subject = null;
	private Map<String, Node> m_list = new HashMap<>();

	@Nonnull
	public Stream<TripleGroup> apply(Triple triple) {
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
}
