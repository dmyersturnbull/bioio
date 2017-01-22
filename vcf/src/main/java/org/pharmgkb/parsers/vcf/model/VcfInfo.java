package org.pharmgkb.parsers.vcf.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import org.pharmgkb.parsers.vcf.model.extra.ReservedInfoProperty;
import org.pharmgkb.parsers.vcf.utils.VcfConversionUtils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A map for the INFO column of a VCF position.
 * This class is just a wrapper for a {@link ImmutableMultimap} that provides conversion utilities (see {@link VcfConversionUtils}).
 * @author Douglas Myers-Turnbull
 */
public class VcfInfo {

	private final ImmutableMultimap<String, String> m_info;

	public VcfInfo(@Nonnull ImmutableMultimap<String, String> info) {
		m_info = info;
	}

	@Nonnull
	public ImmutableMultimap<String, String> getMap() {
		return m_info;
	}

	public ImmutableCollection<String> get(@Nonnull String key) {
		return m_info.get(key);
	}

	public ImmutableCollection<String> get(@Nonnull ReservedInfoProperty key) {
		return m_info.get(key.getId());
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T> ImmutableCollection<T> getConverted(@Nonnull ReservedInfoProperty key) {
		return ImmutableList.copyOf( // needed for consistency of API
				m_info.get(key.getId()).stream()
						.map(s -> (T) VcfConversionUtils.convertProperty(key, Optional.of(s)).get())
						.collect(Collectors.toList())
		);
	}

	public boolean containsValue(@Nonnull String value) {
		return m_info.containsValue(value);
	}

	@Nonnull
	public ImmutableCollection<Map.Entry<String, String>> entries() {
		return m_info.entries();
	}

	@Nonnull
	public ImmutableSet<String> keySet() {
		return m_info.keySet();
	}

	public boolean isEmpty() {
		return m_info.isEmpty();
	}

	@Nonnull
	public ImmutableCollection<String> values() {
		return m_info.values();
	}

	public boolean containsKey(@Nonnull String key) {
		return m_info.containsKey(key);
	}

	public boolean containsKey(@Nonnull ReservedInfoProperty key) {
		return m_info.containsKey(key.getId());
	}

	@Nonnull
	public ImmutableMultiset<String> keys() {
		return m_info.keys();
	}

	@Nonnegative
	public int size() {
		return m_info.size();
	}

	public boolean containsEntry(@Nonnull String key, @Nonnull String value) {
		Preconditions.checkNotNull(key, "Info key cannot be null");
		return m_info.containsEntry(key, value);
	}

	@Nonnull
	public ImmutableMap<String, Collection<String>> asMap() {
		return m_info.asMap();
	}

	@Nonnull
	public ImmutableMultimap<String, String> inverse() {
		return m_info.inverse();
	}

	@Override
	public String toString() {
		return m_info.toString();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfInfo vcfInfo = (VcfInfo) o;
		return Objects.equals(m_info, vcfInfo.m_info);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_info);
	}
}
