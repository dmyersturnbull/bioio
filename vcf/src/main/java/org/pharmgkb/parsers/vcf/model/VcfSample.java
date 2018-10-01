package org.pharmgkb.parsers.vcf.model;

import com.google.common.base.*;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.vcf.model.extra.ReservedFormatProperty;
import org.pharmgkb.parsers.vcf.utils.VcfConversionUtils;
import org.pharmgkb.parsers.vcf.utils.VcfPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * The data of a single sample in a VCF position (non-metadata and non-header) line.
 * This class is just a wrapper for an ordered map of sample properties.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfSample {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final ImmutableMap<String, String> m_properties;

	public VcfSample(@Nonnull Builder builder) {
		m_properties = ImmutableMap.copyOf(builder.m_properties);
	}

	@Nonnull
	public Optional<String> get(@Nonnull String key) {
		return Optional.ofNullable(m_properties.get(key));
	}

	@Nonnull
	public Optional<String> get(@Nonnull ReservedFormatProperty key) {
		return Optional.ofNullable(m_properties.get(key.getId()));
	}

	/**
	 * Gets the property and converts it to the correct class; see {@link VcfConversionUtils}.
	 */
	@Nonnull
	public Optional<?> getConverted(@Nonnull ReservedFormatProperty key) {
		return VcfConversionUtils.convertProperty(key, Optional.ofNullable(m_properties.get(key.getId())));
	}

	@Nonnull
	public ImmutableCollection<String> values() {
		return m_properties.values();
	}

	public ImmutableSet<Map.Entry<String, String>> entrySet() {
		return m_properties.entrySet();
	}

	public ImmutableSet<String> keySet() {
		return m_properties.keySet();
	}

	public boolean containsKey(@Nonnull ReservedFormatProperty key) {
		return m_properties.containsKey(key.getId());
	}

	public boolean containsKey(@Nonnull String key) {
		return m_properties.containsKey(key);
	}

	public boolean containsValue(@Nonnull String value) {
		return m_properties.containsValue(value);
	}

	public boolean isEmpty() {
		return m_properties.isEmpty();
	}

	public int size() {
		return m_properties.size();
	}

	public void forEach(@Nonnull BiConsumer<? super String, ? super String> action) {
		m_properties.forEach(action);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfSample vcfSample = (VcfSample) o;
		return Objects.equal(m_properties, vcfSample.m_properties);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(m_properties);
	}

	@Nonnull
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("properties",
						m_properties.entrySet().stream()
						.map(e -> e.getKey() + "=" + e.getValue())
						.collect(Collectors.joining(","))
				)
				.toString();
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<VcfSample> {

		private static final Splitter sf_equal = Splitter.on("=");
		private LinkedHashMap<String, String> m_properties = new LinkedHashMap<>();

		public Builder() {

		}

		/**
		 * This is a weird constructor provided only for use by the parser.
		 */
		public Builder(@Nonnull Collection<String> keys, @Nonnull Collection<String> values) {
			Preconditions.checkNotNull(keys, "Set of keys cannot be null");
			Preconditions.checkNotNull(values, "Set of values cannot be null");
			// apparently, trailing fields can be dropped
			Preconditions.checkArgument(keys.size() >= values.size(), "Number of FORMAT properties (" + keys.size()
					+ ") is less than the number of SAMPLE values (" + values.size() + ")");
			Iterator<String> formats = keys.iterator();
			Iterator<String> props = values.iterator();
			while (formats.hasNext() && props.hasNext()) {
				m_properties.put(formats.next(), props.next());
			}
		}

		public Builder(@Nonnull Builder builder) {
			Preconditions.checkNotNull(builder, "Builder cannot be null");
			builder.m_properties.forEach((key, value) -> m_properties.put(key, value));
		}

		public Builder(@Nonnull VcfSample sample) {
			Preconditions.checkNotNull(sample, "VcfSample cannot be null");
			sample.m_properties.forEach((key, value) -> m_properties.put(key, value));
		}

		@Nonnull
		public Builder put(@Nonnull String key, @Nullable String value) {
			Preconditions.checkNotNull(key, "Sample key cannot be null");
			return put(key, Optional.ofNullable(value));
		}

		@Nonnull
		public Builder put(@Nonnull String key, @Nonnull Optional<String> value) {
			Preconditions.checkNotNull(key, "Sample key cannot be null");
			Preconditions.checkNotNull(value, "Sample value cannot be null");
			Preconditions.checkArgument(VcfPatterns.SINGLE_FORMAT_PATTERN.matcher(key).matches());
			if (key.equals(ReservedFormatProperty.Genotype.getId()) && !m_properties.isEmpty()) {
				sf_logger.warn("VCF specification requires GT to be the first key in the FORMAT/SAMPLE fields if it is present");
			}
			value.ifPresent(s -> m_properties.put(key, s));
			return this;
		}

		@Nonnull
		@Override
		public VcfSample build() {
			return new VcfSample(this);
		}
	}
}
