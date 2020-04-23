package org.pharmgkb.parsers.vcf.utils;

import org.pharmgkb.parsers.model.GeneralizedBigDecimal;
import org.pharmgkb.parsers.vcf.model.metadata.VcfFormatType;
import org.pharmgkb.parsers.vcf.model.metadata.VcfInfoType;
import org.pharmgkb.parsers.vcf.model.extra.ReservedProperty;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Static methods for converting VCF strings to their expected types.
 * @author Douglas Myers-Turnbull
 */
public class VcfConversionUtils {

	private VcfConversionUtils() {}

	/**
	 * Converts a String representation of a property into a more useful type.
	 * Specifically, can return:
	 * <ul>
	 *   <li>String</li>
	 *   <li>Long</li>
	 *   <li>GeneralizedBigDecimal</li>
	 *   <li>The Boolean true (for flags)</li>
	 *   <li>A List of any of the above types</li>
	 * </ul>
	 */
	@Nonnull
	public static <T> Optional<T> convertProperty(@Nonnull ReservedProperty key, @Nonnull Optional<String> value) {
		return convertProperty(key.getType(), value, key.isList());
	}

	/**
	 * @see #convertProperty(ReservedProperty, Optional)
	 */
	@SuppressWarnings("unchecked")
	@Nonnull
	public static <T> Optional<T> convertProperty(@Nonnull Class<?> clas, @Nonnull Optional<String> value, boolean isList) {
		if (!value.isPresent()) {
			return Optional.empty();
		}
		if (!isList) {
			try {
				return Optional.of((T) convertElement(clas, value));
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Wrong type specified", e);
			}
		}
		List<Object> list = new ArrayList<>();
		for (String part : value.get().split(",")) {
			list.add(convertElement(clas, Optional.of(part)));
		}
		try {
			return Optional.of((T) list);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Wrong type specified", e);
		}
	}

	@Nonnull
	public static <T> Optional<T> convertProperty(@Nonnull VcfFormatType type, @Nonnull Optional<String> value) {
		Class<?> clas = switch (type) {
			case Integer -> Long.class;
			case Float -> GeneralizedBigDecimal.class;
			case Character -> Character.class;
			case String -> String.class;
		};
		return convertProperty(clas, value, false);
	}

	@Nonnull
	public static <T> Optional<T> convertProperty(@Nonnull VcfInfoType type, @Nonnull Optional<String> value) {
		Class<?> clas = switch (type) {
			case Integer -> Long.class;
			case Float -> GeneralizedBigDecimal.class;
			case Character -> Character.class;
			case String -> String.class;
			case Flag -> Boolean.class;
		};
		return convertProperty(clas, value, false);
	}

	@Nonnull
	private static Optional<?> convertElement(@Nonnull Class<?> clas, @Nonnull Optional<String> value) {
		if (!value.isPresent()) {
			return Optional.empty();
		}
		String val = value.get();
		if (clas == String.class) {
			return Optional.of(value);
		} else if (clas == Character.class) {
			if (val.length() == 1) {
				return Optional.of(value);
			} else {
				throw new IllegalArgumentException("Invalid character value '" + value + "'");
			}
		} else if (clas == Boolean.class) {
			if (val.equals("0") || val.equalsIgnoreCase("false")) {
				return Optional.of(false);
			}
			if (val.equals("1") || val.equalsIgnoreCase("true")) {
				return Optional.of(true);
			}
			throw new IllegalArgumentException("Invalid boolean value: '" + value + "'");

		} else if (clas == GeneralizedBigDecimal.class) {
			try {
				return Optional.of(new GeneralizedBigDecimal(val));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Expected float; got " + value);
			}
		} else if (clas == Long.class) {
			try {
				return Optional.of(Long.parseLong(val));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Expected integer; got " + value);
			}
		}
		throw new UnsupportedOperationException("Type " + clas + " unrecognized");
	}

}
