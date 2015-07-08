package org.pharmgkb.parsers.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attempts to ensure consistency in chromosome names.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class ChromosomeName implements Comparable<ChromosomeName> {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final Pattern sf_pattern = Pattern.compile("^(?:chr)?(\\d{1,2}|X|Y|M|(?:MT))(_[A-Z]+[0-9]+v\\d+?(?:_(?:random)|(?:alt)))?$");

	private final String m_originalName;
	private final String m_name;

	public ChromosomeName(@Nonnull String name) {
		Matcher matcher = sf_pattern.matcher(name);
		if (matcher.matches()) {
			String chrName = matcher.group(1).equals("MT")? "M" : matcher.group(1); // dbSNP and Ensembl use this, but neither GRC nor UCSC do
			m_name = "chr" + chrName + (matcher.group(2)==null? "" : matcher.group(2));
		} else {
			sf_logger.warn("Chromosome name {} is not standardized", name);
			m_name = name;
		}
		m_originalName = name;
	}

	@Nonnull
	public String getOriginalName() {
		return m_originalName;
	}

	@Nonnull
	@Override
	public String toString() {
		return m_name;
	}

	public boolean isMitochondial() {
		return m_name.equals("chrM");
	}

	public boolean isX() {
		return m_name.equals("chrX");
	}

	public boolean isY() {
		return m_name.equals("chrY");
	}

	public boolean isNonstandard() {
		return m_name.contains("_");
	}

	public boolean isAlt() {
		return m_name.endsWith("alt");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ChromosomeName that = (ChromosomeName) o;
		return m_name.equals(that.m_name);
	}

	@Override
	public int hashCode() {
		return m_name.hashCode();
	}

	@Override
	public int compareTo(@Nonnull ChromosomeName o) {
		return m_name.compareTo(o.m_name);
	}
}
