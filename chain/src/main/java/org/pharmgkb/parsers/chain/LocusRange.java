package org.pharmgkb.parsers.chain;

import org.pharmgkb.parsers.Strand;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A range of {@link Locus loci}. Contains a start and an end, and provides methods for determining overlap.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class LocusRange {

    private static Pattern sf_pattern = Pattern.compile("^(chr(?:(?:\\d{1,2})|X|Y|M))\\(([\\+-])\\):(\\d+)-(\\d+)$");

    private final Locus m_start;
    private final Locus m_end;

	@Nonnull
    public Strand getStrand() {
        return m_start.getStrand();
    }

	@Nonnull
    public ChromosomeName getChromosome() {
        return m_start.getChromosome();
    }

    public LocusRange(@Nonnull Locus start, @Nonnull Locus end) {
        if (!start.getChromosome().equals(end.getChromosome())) {
            throw new IllegalArgumentException("Start and end must have the same chromosome");
        }
        if (start.getStrand() != end.getStrand()) {
            throw new IllegalArgumentException("Start and end must belong to the same strand");
        }
        if (start.getPosition() >= end.getPosition()) {
            throw new IllegalArgumentException("End " + end + " was not after start " + start);
        }
        m_start = start;
        m_end = end;
    }

	@Nonnull
    public Locus getStart() {
        return m_start;
    }

	@Nonnull
    public Locus getEnd() {
        return m_end;
    }

    /**
     * @throws java.lang.IllegalArgumentException If the strand of {@code locus} does not match the strand of this range
     */
    public boolean contains(@Nonnull Locus locus) {
        if (locus.getStrand() != getStrand()) {
	        throw new IllegalArgumentException("Cannot compare loci " + "belonging to different strands");
        }
        return locus.getChromosome().equals(getChromosome()) && locus.getPosition() >= m_start.getPosition() && locus.getPosition() <= m_end.getPosition();
    }

    public boolean overlapsWith(@Nonnull LocusRange locusRange) {
        return calcOverlappingDensity(locusRange) > 0;
    }

	/**
	 * @return The number of overlapping positions <strong>minus 1</strong>
	 */
    public long calcOverlappingDensity(@Nonnull LocusRange locusRange) {
        if (locusRange.getStrand() != getStrand()) {
	        throw new IllegalArgumentException("Cannot compare loci belonging to different strands");
        }
        if (!locusRange.getChromosome().equals(getChromosome())) return 0;
        return Math.min(m_end.getPosition(), locusRange.getEnd().getPosition()) - Math.max(m_start.getPosition(), locusRange.getStart().getPosition());
    }

    public boolean isCompatibleWith(@Nullable LocusRange range) {
        return range != null && getChromosome().equals(range.getChromosome()) && getStrand() == range.getStrand();
    }

	@Nonnegative
    public long length() {
        return m_end.getPosition() - m_start.getPosition();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocusRange that = (LocusRange) o;
        return m_end.equals(that.m_end) && m_start.equals(that.m_start);
    }

    @Override
    public int hashCode() {
	    return Objects.hash(m_start, m_end);
    }

    @Override
    public String toString() {
        return m_start.getChromosome() + "(" + m_start.getStrand().getSymbol() + "):" + m_start.getPosition() + '-' + m_end.getPosition();
    }

	@Nonnull
    public static LocusRange parse(@Nonnull String string) {
        Matcher matcher = sf_pattern.matcher(string);
        if (!matcher.matches()) {
	        throw new IllegalArgumentException("String " + string + " is not a valid locus range");
        }
        String chromosome = matcher.group(1);
        Optional<Strand> strand = Strand.lookupBySymbol(matcher.group(2));
        if (strand.isPresent()) {
            long startPosition = Long.parseLong(matcher.group(3));
            long stopPosition = Long.parseLong(matcher.group(4));
            Locus start = new Locus(new ChromosomeName(chromosome), startPosition, strand.get());
            Locus stop = new Locus(new ChromosomeName(chromosome), stopPosition, strand.get());
            return new LocusRange(start, stop);
        }
        throw new IllegalArgumentException("String " + string + " is not a valid locus range");
    }

}