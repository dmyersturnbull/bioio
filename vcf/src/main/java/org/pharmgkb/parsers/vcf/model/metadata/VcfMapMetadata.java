package org.pharmgkb.parsers.vcf.model.metadata;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.pharmgkb.parsers.vcf.utils.PropertyMapBuilder;
import org.pharmgkb.parsers.vcf.utils.VcfEscapers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A VCF metdata line that is a comma-separated list of key-value pairs, like {@code ##XXX=<A=x,B=y>}.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public abstract class VcfMapMetadata implements VcfMetadata {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final long serialVersionUID = -1475818005852776833L;

	private VcfMetadataType m_type;
	private ImmutableMap<String, String> m_properties;

	public VcfMapMetadata(@Nonnull VcfMetadataType type, @Nonnull Map<String, String> properties) {
		m_type = type;
		PropertyMapBuilder builder = new PropertyMapBuilder();
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			String value = ifUnquoted(VcfEscapers.METADATA::unescape, entry.getValue());
			builder.put(entry.getKey(), value);
		}
		m_properties = builder.build();
		check();
	}

	/**
	 * @throws IllegalArgumentException If the string is not quoted
	 */
	@Nonnull
	public Optional<String> getPropertyUnquoted(@Nonnull String key) {
		Optional<String> opt = Optional.ofNullable(m_properties.get(key));
		Preconditions.checkArgument(!opt.isPresent() || opt.get().startsWith("\"") && opt.get().endsWith("\""), "String " + key + " is not quoted");
		return opt.map(s -> s.substring(1, s.length() - 1));
	}

	@Nonnull
	public Optional<String> getPropertyRaw(@Nonnull String key) {
		return Optional.ofNullable(m_properties.get(key));
	}

	@Nonnull
	public ImmutableSet<String> getPropertyKeys() {
		return m_properties.keySet();
	}

	private void check() {
		for (Map.Entry<String, String> entry : m_properties.entrySet()) {
			if (entry.getKey().contains("\n") || entry.getValue().contains("\n")) {
				throw new IllegalArgumentException("Info [[[" + entry.getKey() + "=" + entry.getValue() + "]]] contains a newline");
			}
		}
	}

	protected void require(@Nonnull String... names) {
		Arrays.asList(names).forEach(s -> {
			if (!m_properties.containsKey(s)) {
				IllegalArgumentException x = new IllegalArgumentException("Missing required property " + s);
				x.addSuppressed(new Exception(toString()));
				throw x;
			}
		});
	}

	/**
	 * Should be used only for base classes.
	 * Logs a warning if this metadata contains a property key not in the array passed.
	 * @param names An array of permitted property keys
	 */
	protected void ensureNoExtras(@Nonnull String... names) {
		Set<String> set = new HashSet<>();
		Collections.addAll(set, names);
		m_properties.keySet().stream().filter(property -> !set.contains(property)).forEach(property ->
				sf_logger.warn("Metadata line contains unexpected property {}", property));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfMapMetadata that = (VcfMapMetadata) o;
		return com.google.common.base.Objects.equal(m_type, that.m_type) &&
				Objects.equal(m_properties, that.m_properties);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(m_type, m_properties);
	}

	@Override
	public String toString() {
		return m_type + ": {"
				+ m_properties.entrySet().stream()
				.map(e -> e.getKey() + "=" + e.getValue())
				.collect(Collectors.joining(", "))
				+ "}";
	}

	@Nonnull
	@Override
	public String toVcfLine() {
		return "##" + m_type.getId() + "=<"
				+ m_properties.entrySet().stream()
				.map(e -> e.getKey() + "=" + ifUnquoted(VcfEscapers.METADATA::escape, e.getValue()))
				.collect(Collectors.joining(","))
				+ ">";
	}

	private static String ifUnquoted(Function<String, String> fn, String value) {
		return value.startsWith("\"") && value.endsWith("\"") ? value.charAt(0) + fn.apply(value.substring(1, value.length() - 1)) + value.charAt(value.length() - 1) : fn.apply(value);
	}

}
