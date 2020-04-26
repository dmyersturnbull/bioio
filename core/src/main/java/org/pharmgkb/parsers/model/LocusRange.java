package org.pharmgkb.parsers.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A range of {@link Locus loci}. Contains a start and an end, and provides methods for determining overlap.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class LocusRange implements Comparable<LocusRange>, Serializable {

    private static final long serialVersionUID = -1452867522785239185L;
    private static Pattern sf_pattern = Pattern.compile("^(chr(?:(?:\\d{1,2})|X|Y|M))\\(([+-])\\):(\\d+)-(\\d+)$");

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

    /**
     * @throws IllegalArgumentException If start and end belong to different chromosomes or strands, or if end comes before start
     */
    public LocusRange(@Nonnull Locus start, @Nonnull Locus end) {
        if (!start.getChromosome().equals(end.getChromosome())) {
            throw new IllegalArgumentException("Start and end must have the same chromosome");
        }
        if (start.getStrand() != end.getStrand()) {
            throw new IllegalArgumentException("Start and end must belong to the same strand");
        }
        if (start.getPosition() > end.getPosition()) {
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
        Preconditions.checkArgument(locus.getStrand() == getStrand(),
                                    "Cannot compare loci " + "belonging to different strands");
        return locus.getChromosome().equals(getChromosome())
                && locus.getPosition() >= m_start.getPosition()
                && locus.getPosition() <= m_end.getPosition();
    }

    public boolean overlapsWith(@Nonnull LocusRange locusRange) {
        return calcOverlappingDensity(locusRange) > 0;
    }

	/**
	 * @return The number of overlapping positions <strong>minus 1</strong>
     * @throws IllegalArgumentException If {@code locusRange} belongs to a different strand
	 */
    public long calcOverlappingDensity(@Nonnull LocusRange locusRange) {
        Preconditions.checkArgument(
                locusRange.getStrand() == getStrand(),
                "Cannot compare loci belonging to different strands"
        );
        if (!locusRange.getChromosome().equals(getChromosome())) return 0;
        return Math.min(m_end.getPosition(), locusRange.getEnd().getPosition())
                - Math.max(m_start.getPosition(), locusRange.getStart().getPosition()
        );
    }

    /**
     * @return True if and only if the chromosomes and strands are the same
     */
    public boolean isCompatibleWith(@Nonnull LocusRange range) {
        return getChromosome().equals(range.getChromosome()) && getStrand() == range.getStrand();
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

    /**
     * @return A string chromosome(strand):start-end; e.g. chr1(+):5-10
     */
    @Override
    public String toString() {
        return m_start.getChromosome() +
                "(" + m_start.getStrand().getSymbol() + ")"
                + ":" + m_start.getPosition()
                + '-' + m_end.getPosition();
    }

    /**
     * Compares the start locus followed by the end locus.
     * @see Locus#compareTo(Locus)
     */
    @Override
    public int compareTo(@Nonnull LocusRange o) {
        return ComparisonChain.start()
                .compare(m_start, o.m_start)
                .compare(m_end, o.m_end)
                .result();
    }

    /**
     * @param string A string in the form chromosome(strand):start-end; e.g. chr1(+):5-10
     */
	@Nonnull
    public static LocusRange parse(@Nonnull String string) {
        Matcher matcher = sf_pattern.matcher(string);
        Preconditions.checkArgument(matcher.matches(), "String " + string + " is not a valid locus range");
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
