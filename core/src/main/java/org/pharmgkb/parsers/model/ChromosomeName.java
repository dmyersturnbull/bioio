package org.pharmgkb.parsers.model;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.lang.invoke.MethodHandles;
import java.util.function.Function;
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

	@Nonnull
	public static ChromosomeName ucscWithFailure(@Nonnull String name) {
		Function<String, String> standardizer = s -> {
			Matcher matcher = sf_pattern.matcher(s);
			Preconditions.checkArgument(matcher.matches(), "Chromosome name " + s + " does not match the required pattern");
			Preconditions.checkArgument(sf_pattern.matcher(s).matches(), "Chromosome name " + s + " is not standardized");
			String chrName = matcher.group(1).equals("MT")? "M" : matcher.group(1); // dbSNP and Ensembl use this, but neither GRC nor UCSC do
			return "chr" + chrName + (matcher.group(2)==null? "" : matcher.group(2));
		};
		return standardized(name, standardizer);
	}

	@Nonnull
	public static ChromosomeName ucscWithWarning(@Nonnull String name) {
		Function<String, String> standardizer = s -> {
			Matcher matcher = sf_pattern.matcher(s);
			if (matcher.matches()) {
				String chrName = matcher.group(1).equals("MT")? "M" : matcher.group(1); // dbSNP and Ensembl use this, but neither GRC nor UCSC do
				return "chr" + chrName + (matcher.group(2)==null? "" : matcher.group(2));
			} else {
				sf_logger.warn("Chromosome name {} is not standardized", s);
				return s;
			}
		};
		return standardized(name, standardizer);
	}

	@Nonnull
	public static ChromosomeName standardized(@Nonnull String name, @Nonnull Function<? super String, String> standardizer) {
		return new ChromosomeName(name, standardizer.apply(name));
	}

	public ChromosomeName(@Nonnull String name) {
		this(name, name);
	}

	private ChromosomeName(@Nonnull String originalName, @Nonnull String standardizedName) {
		Preconditions.checkNotNull(originalName);
		Preconditions.checkNotNull(standardizedName);
		m_originalName = originalName;
		m_name = standardizedName;
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

	// TODO these aren't guaranteed to work for every convention

	public boolean isMitochondial() {
		return m_name.equals("chrM") || m_name.equals("M") || m_name.equals("MT") || m_name.equals("chrMT");
	}

	public boolean isX() {
		return m_name.equals("chrX") || m_name.equals("X");
	}

	public boolean isY() {
		return m_name.equals("chrY") || m_name.equals("Y");
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
