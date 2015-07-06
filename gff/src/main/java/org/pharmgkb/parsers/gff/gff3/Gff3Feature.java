package org.pharmgkb.parsers.gff.gff3;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.pharmgkb.parsers.gff.BaseGffFeature;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A line of GFF3 data, which consists of key-value pairs, several of which are required.
 * <a href="http://www.sequenceontology.org/gff3.shtml">http://www.sequenceontology.org/gff3.shtml</a>
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class Gff3Feature extends BaseGffFeature {

	private final ImmutableMap<String, List<String>> m_attributes;

	private Gff3Feature(@Nonnull Builder builder) {
		super(builder);
		m_attributes = ImmutableMap.copyOf(builder.m_attributes);
	}

	@Nonnull
	public List<String> getAttributes(@Nonnull Gff3Attribute key) {
		return getAttributes(key.getId());
	}

	@Nonnull
	public List<String> getAttributes(@Nonnull String key) {
		List<String> found = m_attributes.get(key);
		return found==null? Collections.<String>emptyList() : found;
	}

	@Nonnull
	public ImmutableSet<ImmutableMap.Entry<String, List<String>>> getAttributes() {
		return ImmutableSet.copyOf(m_attributes.entrySet());
	}

	public static class Builder extends BaseGffFeature.Builder<Gff3Feature, Builder> {

		private Map<String, List<String>> m_attributes;

		@Nonnull
		public Builder(@Nonnull String id, @Nonnull String type, @Nonnegative long start, @Nonnegative long end) {
			super(id, type, start, end);
			m_attributes = new TreeMap<>(); // for obvious sort order
		}

		@Nonnull
		public Builder putAttributes(@Nonnull String key, @Nonnull List<String> values) {
			m_attributes.put(key, values);
			return this;
		}

		@Nonnull
		public Builder clearAttributes() {
			m_attributes.clear();
			return this;
		}

		@Nonnull
		public Gff3Feature build() {
			return new Gff3Feature(this);
		}
	}

}
