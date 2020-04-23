package org.pharmgkb.parsers.vcf.model.metadata;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

/**
 * The {@code NUMBER property} of a ##FORMAT metadata line.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfFormatNumber {

	private final Optional<VcfNumberFlag> m_flag;
	private final Optional<Long> m_number;

	public VcfFormatNumber(@Nonnull String string) {
		Optional<VcfNumberFlag> flag = VcfNumberFlag.fromId(string);
		if (flag.isPresent()) {
			m_number = Optional.empty();
			m_flag = flag;
		} else {
			try {
				m_number = Optional.of(Long.parseLong(string));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid NUMBER field " + string, e);
			}
			m_flag = Optional.empty();
		}
	}

	@Nonnull
	public Optional<VcfNumberFlag> asReservedFlag() {
		return m_flag;
	}

	@Nonnull
	public Optional<Long> asNumber() {
		return m_number;
	}

	@Nonnull
	@Override
	public String toString() {
		//noinspection OptionalGetWithoutIsPresent
		return m_flag.map(VcfNumberFlag::getId).orElseGet(() -> String.valueOf(m_number.get()));
	}

}
