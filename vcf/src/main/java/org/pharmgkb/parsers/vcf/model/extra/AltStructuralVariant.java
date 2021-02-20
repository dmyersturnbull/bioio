package org.pharmgkb.parsers.vcf.model.extra;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A strictly validated VCF metadata ALT code of the form:
 * {@code
 *   ##ALT=<ID=type,Description=description>
 * }
 * Where {@code ID} is a colon-delimited list of identifiers. Some of these identifiers are reserved, as coded in the
 * {@link ReservedStructuralVariantCode} class. The first identifier (at level 0) is required to be reserved.
 * As explicitly stated in the spec, these codes are case-sensitive.
 * Example:
 * {@code
 *   AltStructuralVariant alt = new AltStructuralVariant("INS:ME:LINE");
 *   alt.getReservedComponent(0); // ReservedStructuralVariantCode.Insertion
 *   alt.getReservedComponent(1); // ReservedStructuralVariantCode.MobileElement
 *   alt.getReservedComponent(2); // null, because it's not a reserved code
 *   alt.getComponent(); // "LINE"
 * }
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class AltStructuralVariant {

	public AltStructuralVariant(@Nonnull List<String> components) {
		if (components instanceof ImmutableList) {
			//noinspection AssignmentOrReturnOfFieldWithMutableType
			m_components = ((ImmutableList<String>)components);
		} else {
			m_components = ImmutableList.copyOf(components);
		}
	}

	private static final Splitter sf_colon = Splitter.on(":");

	private final ImmutableList<String> m_components;
	private ReservedStructuralVariantCode m_topLevel; // effectively final

	/**
	 * @param string The full code (e.g. INS:ME:LINE:type-a1)
	 */
	public AltStructuralVariant(@Nonnull String string) {

		if (string.isEmpty()) {
			throw new IllegalArgumentException("Structural variant code must not be empty");
		}

		List<String> components = sf_colon.splitToList(string);
		List<String> comps = new ArrayList<>(components.size());

		//noinspection NonConstantStringShouldBeStringBuffer
		String stringFromTop = "";
		int level = 0;
		// TODO: This is extremely confusing
		for (; level < components.size(); level++) {
			stringFromTop += components.get(level);
			Optional<ReservedStructuralVariantCode> topLevel = ReservedStructuralVariantCode.fromId(stringFromTop);
			topLevel.ifPresent(reservedStructuralVariantCode -> m_topLevel = reservedStructuralVariantCode);
			comps.add(components.get(level));
		}

		// Make sure the top-level code exists
		if (m_topLevel == null) {
			throw new IllegalArgumentException("Top-level structural variant code must be a top-level reserved code (e.g. DEL or CNV)");
		}

		m_components = ImmutableList.copyOf(comps);
	}

	@Nonnull
	public ReservedStructuralVariantCode getTopLevelCode() {
		return m_topLevel;
	}

	/**
	 * @return The list of codes in order from level 0 to level n; for example ("INS", "ME", "LINE")
	 */
	@Nonnull
	public ImmutableList<String> getComponents() {
		return m_components;
	}

	/**
	 * @return The code at the specified level (e.g. CNV)
	 * @throws ArrayIndexOutOfBoundsException If fewer levels exist
	 */
	@Nonnull
	public String getComponent(@Nonnegative int level) {
		return m_components.get(level);
	}

	/**
	 * @return The original string (e.g. INS:ME:LINE:type-a1)
	 */
	@Override
	@Nonnull
	public String toString() {
		return String.join(":", m_components);
	}

}
