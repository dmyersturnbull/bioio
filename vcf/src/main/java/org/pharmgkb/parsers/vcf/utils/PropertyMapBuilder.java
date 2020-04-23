package org.pharmgkb.parsers.vcf.utils;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.pharmgkb.parsers.ObjectBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;
import java.util.Optional;

/**
 * A builder for string-to-string maps.
 * @author Douglas Myers-Turnbull
 */
@NotThreadSafe
public class PropertyMapBuilder extends ImmutableMap.Builder<String, String> implements ObjectBuilder<ImmutableMap<String, String>> {

	public PropertyMapBuilder() {}

	public PropertyMapBuilder(@Nonnull Map<String, String> map) {
		map.forEach(this::put);
	}

	@Nonnull
	public PropertyMapBuilder put(@Nonnull String key, @Nonnull Optional<String> value) {
		Preconditions.checkNotNull(key, "Key cannot be null");
		Preconditions.checkNotNull(value, "Value cannot be null");
		value.ifPresent(s -> super.put(key, s));
		return this;
	}

	@Nonnull
	@Override
	public PropertyMapBuilder put(@Nonnull String key, @Nullable String value) {
		Preconditions.checkNotNull(key, "Key cannot be null");
		if (value != null) {
			super.put(key, value);
		}
		return this;
	}
}
