package org.pharmgkb.parsers.vcf;

import com.google.common.base.Splitter;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.LineParser;
import org.pharmgkb.parsers.model.GeneralizedBigDecimal;
import org.pharmgkb.parsers.vcf.model.VcfPosition;
import org.pharmgkb.parsers.vcf.model.VcfSample;
import org.pharmgkb.parsers.vcf.utils.VcfEscapers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses VCF position lines; that is, every line that does not begin with a {@code #}.
 * Unescapes strings automatically using {@link VcfEscapers}.
 * To validate that these lines match the metadata, see {@link VcfValidator}.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class VcfDataParser implements LineParser<VcfPosition> {

	private static final long sf_logEvery = 10000;

	private static final Splitter sf_comma = Splitter.on(",");
	private static final Splitter sf_tab = Splitter.on("\t");
	private static final Splitter sf_colon = Splitter.on(":");
	private static final Splitter sf_semicolon = Splitter.on(";");

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0l);

	@Nonnull
	@Override
	public Stream<VcfPosition> parseAll(@Nonnull Stream<String> stream) throws IOException, BadDataFormatException {
		return stream.filter(s -> !s.startsWith("#")).map(this);
	}

	@Nonnull
	@Override
	public VcfPosition apply(@Nonnull String line) throws BadDataFormatException {

		if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
			sf_logger.debug("Reading line #{}", m_lineNumber);
		}

		if (line.startsWith("#")) {
			throw new BadDataFormatException("Line looks like metadata on line #" + m_lineNumber.get() + ": [[[" + line + "]]]");
		}

		List<String> data = sf_tab.splitToList(line);

		try {

			// CHROM
			String chromosome = VcfEscapers.CHROMOSOME.unescape(data.get(0));

			// POS
			long position;
			try {
				position = Long.parseLong(data.get(1)) - 1; // VCF is 1-based
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Position " + data.get(1) + " is not numerical");
			}

			// REF
			String ref = data.get(3);

			VcfPosition.Builder builder = new VcfPosition.Builder(chromosome, position, ref);

			// ID
			if (!data.get(2).equals(".")) {
				builder.addIds(
						sf_semicolon.splitToList(data.get(2)).stream()
								.map(VcfEscapers.ID::unescape)
								.collect(Collectors.toList())
				);
			}

			// ALT
			if (!data.get(4).equals(".")) {
				builder.addAlts(sf_comma.splitToList(data.get(4)));
			}

			// QUAL
			if (!data.get(5).equals(".")) {
				try {
					builder.setQuality(Optional.of(new GeneralizedBigDecimal(data.get(5))));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("QUAL is not a number", e);
				}
			}

			// FILTER
			if (!data.get(6).equals(".")) {
				Stream<String> unescaped = sf_semicolon.splitToList(data.get(6)).stream()
						.map(VcfEscapers.FILTER::unescape);
				builder.addFilters(unescaped.collect(Collectors.toList()));
			}

			// INFO
			Set<String> keysUsed = new HashSet<>();
			if (!data.get(7).equals(".")) {
				List<String> props = sf_semicolon.splitToList(data.get(7));
				for (String prop : props) {
					int index = prop.indexOf('=');
					String key, value;
					if (index == -1) {
						key = prop;
						value = ""; // weird!
					} else {
						key = prop.substring(0, index);
						value = prop.substring(index + 1);
					}
					if (keysUsed.contains(key)) {
						throw new BadDataFormatException("Key " + key + " appears more than once in the INFO field");
					}
					Stream<String> unescaped = sf_comma.splitToList(value).stream()
							.map(VcfEscapers.INFO_VALUE::unescape);
					builder.putInfo(key, unescaped.collect(Collectors.toList()));
					keysUsed.add(key);
				}
			}

			// FORMAT
			List<String> format = new ArrayList<>(0);
			if (data.size() >= 9) {
				format = sf_colon.splitToList(data.get(8));
			}
			builder.addFormats(format);

			// samples
			for (int x = 9; x < data.size(); x++) {
				Stream<String> unescaped = sf_colon.splitToList(data.get(x)).stream()
						.map(VcfEscapers.SAMPLE::unescape);
				builder.addSample(new VcfSample.Builder(format, unescaped.collect(Collectors.toList())).build());
			}

			return builder.build();

		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			throw new BadDataFormatException("Bad data format on line #" + m_lineNumber.get()
					+ "; line is [[[" + line + "]]]", e);
		} catch (RuntimeException e) {
			// this is a little weird, but it's helpful
			// not that we're not throwing a BadDataFormatException because we don't expect AIOOB, e.g.
			e.addSuppressed(new RuntimeException("Failed on line " + m_lineNumber));
			throw e;
		}
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

	@Override
	public String toString() {
		return "VcfDataParser{" +
				"lineNumber=" + m_lineNumber.get() +
				'}';
	}
}
