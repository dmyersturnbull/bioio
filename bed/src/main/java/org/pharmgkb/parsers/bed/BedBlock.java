package org.pharmgkb.parsers.bed;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;

/**
 * The start and end of a block in a {@link org.pharmgkb.parsers.bed.BedFeature}.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class BedBlock implements Serializable {

	private static final long serialVersionUID = 3728077750509042933L;
	private final long m_start;

	private final long m_end;

	public BedBlock(@Nonnegative long start, @Nonnegative long end) {
		Preconditions.checkArgument(start > -1, "Block start" + start + " is negative");
		Preconditions.checkArgument(end > -1, "Block end" + end + " is negative");
		Preconditions.checkArgument(start <= end, "Block cannot start before it ends; start is " + start + " but end is " + end);
		m_start = start;
		m_end = end;
	}

	@Nonnegative
	public long getStart() {
		return m_start;
	}

	@Nonnegative
	public long getLength() {
		return m_end - m_start;
	}

	@Nonnegative
	public long getEnd() {
		return m_end;
	}

	@Override
	public String toString() {
		return m_start + "-" + m_end;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BedBlock bedBlock = (BedBlock) o;
		return Objects.equals(m_start, bedBlock.m_start) && Objects.equals(m_end, bedBlock.m_end);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_start, m_end);
	}
}
