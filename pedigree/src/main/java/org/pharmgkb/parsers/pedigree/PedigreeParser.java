package org.pharmgkb.parsers.pedigree;

import com.google.common.collect.ImmutableSet;
import org.pharmgkb.parsers.LineStructureParser;
import org.pharmgkb.parsers.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Parses a pedigree file in LINKAGE or QTDT format. This is a line-by-line, whitespace-delimited format usually given
 * the .ped extension.
 * <a href="http://www.helsinki.fi/~tsjuntun/autogscan/pedigreefile.html">http://www.helsinki.fi/~tsjuntun/autogscan/pedigreefile.html</a>
 * <a href="http://www.sph.umich.edu/csg/abecasis/merlin/tour/input_files.html">http://www.sph.umich.edu/csg/abecasis/merlin/tour/input_files.html</a>
 *
 * For example:
 <pre>
 gen 1:        [1] ----------- (2)             [3]
 |                       |
 gen 2:               (4) ---------------------/
 |           |
 gen 3:                      (5)         [6]


 1   1   0  0  1   1      x   3 3   x x
 1   2   0  0  2   1      x   4 4   x x
 1   3   0  0  1   1      x   1 2   x x
 1   4   1  2  2   1      x   4 3   x x
 1   5   3  4  2   2  1.234   1 3   2 2
 1   6   3  4  1   2  4.321   2 4   2 2
 * </pre>
 * {@code
 *     Pedigree pedigree = new PedigreeParser.Builder().build().apply(Files.lines(file));
 *     Individual five = pedigree.getFamily("1").find("5");
 *     five.getData(); // returns {"4.321", "2", "4", "2", "2"}
 * }
 * @author Douglas Myers-Turnbull
 */
public class PedigreeParser implements LineStructureParser<Pedigree> {

	private static final long sf_logEvery = 10000;
	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String m_noParentMarker;
	private final Pattern m_fieldSeparator;
	private final ImmutableSet<String> m_femaleCodes;
	private final ImmutableSet<String> m_maleCodes;
	private final ImmutableSet<String> m_unknownCodes;
	private final boolean m_parentsAddedFirst;

	private PedigreeParser(@Nonnull Builder builder) {
		m_noParentMarker = builder.m_noParentMarker;
		m_fieldSeparator = builder.m_fieldSeparator;
		m_femaleCodes = ImmutableSet.copyOf(builder.m_femaleCodes);
		m_maleCodes = ImmutableSet.copyOf(builder.m_maleCodes);
		m_unknownCodes = ImmutableSet.copyOf(builder.m_unknownCodes);
		m_parentsAddedFirst = builder.m_parentsAddedFirst;
	}

	@Nonnull
	@Override
	public Pedigree apply(@Nonnull Stream<String> stream) {
		final AtomicLong lineNumber = new AtomicLong(0l);
		PedigreeBuilder builder = new PedigreeBuilder(m_parentsAddedFirst);
		stream.forEach(line -> {

			if (lineNumber.incrementAndGet() % sf_logEvery == 0) {
				sf_logger.debug("Reading line #{}", lineNumber);
			}

			String[] parts = m_fieldSeparator.split(line);
			if (parts.length < 5) {
				throw new IllegalArgumentException("Line #" + lineNumber.get() + " contains fewer than 5 columns");
			}
			String fatherId = null;
			if (!parts[2].equals(m_noParentMarker)) {
				fatherId = parts[2];
			}
			String motherId = null;
			if (!parts[3].equals(m_noParentMarker)) {
				motherId = parts[3];
			}
			Sex sex;
			if (m_femaleCodes.contains(parts[4])) {
				sex = Sex.FEMALE;
			} else if (m_maleCodes.contains(parts[4])) {
				sex = Sex.MALE;
			} else if (m_unknownCodes.contains(parts[4])) {
				sex = Sex.UNKNOWN;
			} else {
				throw new IllegalArgumentException("Sex " + parts[4] + " not recognized");
			}
			List<String> info = new ArrayList<>(parts.length - 5);
			if (parts.length > 5) {
				info.addAll(Arrays.asList(parts).subList(6, parts.length));
			}
			builder.add(parts[0], parts[1], fatherId, motherId, sex, info);
		});
		return builder.build();
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<PedigreeParser> {

		private String m_noParentMarker = "0";
		private Pattern m_fieldSeparator = Pattern.compile("\\s+");
		private Set<String> m_femaleCodes = new HashSet<>();
		private Set<String> m_maleCodes = new HashSet<>();
		private Set<String> m_unknownCodes = new HashSet<>();
		private boolean m_parentsAddedFirst;

		public Builder() {
			m_maleCodes.add("1");
			m_femaleCodes.add("2");
			m_unknownCodes.add("3");
		}

		/**
		 * @param noParentMarker The marker that means the individual has no parents in the family; this is usually 0
		 */
		@Nonnull
		public Builder setNoParentMarker(@Nonnull String noParentMarker) {
			m_noParentMarker = noParentMarker;
			return this;
		}

		/**
		 * @param fieldSeparator The regex pattern that separates columns; this is usually whitespace
		 */
		@Nonnull
		public Builder setFieldSeparator(@Nonnull Pattern fieldSeparator) {
			m_fieldSeparator = fieldSeparator;
			return this;
		}

		/**
		 * @param femaleCodes The strings in the sex column that mean female; this is usually just 1
		 */
		@Nonnull
		public Builder setFemaleCodes(@Nonnull Set<String> femaleCodes) {
			m_femaleCodes = femaleCodes;
			return this;
		}

		/**
		 * @param maleCodes The strings in the sex column that mean male; this is usually just 0
		 */
		@Nonnull
		public Builder setMaleCodes(@Nonnull Set<String> maleCodes) {
			m_maleCodes = maleCodes;
			return this;
		}

		/**
		 * @param unknownCodes The strings in the sex column that mean unknown/other; this is set to 3 by default;
		 *                           to forbid the unknown sex, set to an empty set
		 */
		@Nonnull
		public Builder setUnknownCodes(@Nonnull Set<String> unknownCodes) {
			m_unknownCodes = unknownCodes;
			return this;
		}

		/**
		 * Speeds up construction, but individuals must be added in order: if non-null, {@code fatherId} and {@code motherId}, must reference individuals that have already been added
		 */
		@Nonnull
		public Builder setParentsAddedFirst() {
			m_parentsAddedFirst = true;
			return this;
		}

		@Nonnull
		public PedigreeParser build() {
			return new PedigreeParser(this);
		}
	}

}
