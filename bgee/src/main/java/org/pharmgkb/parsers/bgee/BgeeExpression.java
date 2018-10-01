package org.pharmgkb.parsers.bgee;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.math.BigDecimal;
import java.util.Objects;


/**
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class BgeeExpression {

	protected final String m_geneId;
	protected final String m_geneName;
	protected final String m_tissueId;
	protected final String m_tissueName;
	protected final String m_stageId;
	protected final String m_stageName;
	protected final boolean m_isExpressed;
	protected final Quality m_quality;
	protected final BigDecimal m_level;
	protected final ImmutableMap<String, String> m_fullInfo;

	public BgeeExpression(String geneId, String geneName, String tissueId, String tissueName, String stageId, String stageName, boolean isExpressed, Quality quality, BigDecimal level, ImmutableMap<String, String> extendedInfo) {
		this.m_geneId = geneId;
		this.m_geneName = geneName;
		this.m_tissueId = tissueId;
		this.m_tissueName = tissueName;
		this.m_stageId = stageId;
		this.m_stageName = stageName;
		this.m_isExpressed = isExpressed;
		this.m_quality = quality;
		this.m_level = level;
		this.m_fullInfo = extendedInfo;
	}

	@Nonnull
	public String geneId() {
		return m_geneId;
	}

	@Nonnull
	public String geneName() {
		return m_geneName;
	}

	@Nonnull
	public String tissueId() {
		return m_tissueId;
	}

	@Nonnull
	public String tissueName() {
		return m_tissueName;
	}

	@Nonnull
	public String stageId() {
		return m_stageId;
	}

	@Nonnull
	public String stageName() {
		return m_stageName;
	}

	public boolean isExpressed() {
		return m_isExpressed;
	}

	@Nonnull
	public Quality getQuality() {
		return m_quality;
	}

	@Nonnegative
	public BigDecimal getLevel() {
		return m_level;
	}

	@Nonnull
	public ImmutableMap<String, String> getFullInfo() {
		return m_fullInfo;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BgeeExpression that = (BgeeExpression) o;
		return Objects.equals(m_fullInfo, that.m_fullInfo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_fullInfo);
	}

	@Override
	public String toString() {
		return BgeeExpression.class.getSimpleName() + "[" + m_fullInfo + "]";
	}
}
