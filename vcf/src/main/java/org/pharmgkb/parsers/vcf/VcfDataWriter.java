package org.pharmgkb.parsers.vcf;

import org.pharmgkb.parsers.LineWriter;
import org.pharmgkb.parsers.model.GeneralizedBigDecimal;
import org.pharmgkb.parsers.vcf.model.VcfPosition;
import org.pharmgkb.parsers.vcf.model.VcfSample;
import org.pharmgkb.parsers.vcf.model.allele.VcfAllele;
import org.pharmgkb.parsers.vcf.utils.VcfEscapers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Writes VCF position lines; that is, every line that does not begin with a {@code #}.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class VcfDataWriter implements LineWriter<VcfPosition> {

	private static final long sf_logEvery = 10000;

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0L);

	@Nonnull
	@Override
	public String apply(@Nonnull VcfPosition position) {
		StringBuilder sb = new StringBuilder(128)
				.append(position.getChromosome())
				.append("\t")
				.append(position.getPosition() + 1) // VCF is 1-based
				.append("\t")
				.append(orDot(
						position.getIds().stream()
								.map(VcfEscapers.ID::escape)
								.collect(Collectors.toList()),
						",")
				)
				.append("\t")
				.append(position.getRef().toVcfString())
				.append("\t")
				.append(orDot(
						position.getAlts().stream()
								.map(VcfAllele::toVcfString)
								.collect(Collectors.toList()),
						",")
				)
				.append("\t")
				.append(position.getQuality()
						.map(GeneralizedBigDecimal::toString)
						.orElse("."))
				.append("\t")
				.append(orDot(
						position.getFilters().stream()
								.map(VcfEscapers.FILTER::escape)
								.collect(Collectors.toList()
								), ";")
				)
				.append("\t")
				.append(
						position.getInfo().asMap().entrySet().stream()
								.map(e -> e.getKey() + (
										Collections.singletonList("").containsAll(e.getValue())?
												""
												: "=" + e.getValue().stream()
														.map(VcfEscapers.INFO_VALUE::escape)
														.collect(Collectors.joining(","))
								))
								.collect(Collectors.joining(";"))
				);
		if (!position.getFormat().isEmpty()) {
			sb.append("\t")
					.append(position.getFormat().stream()
							.map(VcfEscapers.FORMAT::escape)
							.collect(Collectors.joining(":")));
			for (VcfSample sample : position.getSamples()) {
				sb.append("\t")
						.append(
								sample.entrySet().stream()
								.map(Map.Entry::getValue)
								.map(VcfEscapers.SAMPLE::escape)
								.collect(Collectors.joining(":"))
						);
			}
		}
		return sb.toString();
	}

	@Nonnull
	private static String orDot(@Nonnull List<String> list, @Nonnull String delimiter) {
		return list.isEmpty()? "." : String.join(delimiter, list);
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

	@Override
	public String toString() {
		return "VcfDataWriter{" +
				"lineNumber=" + m_lineNumber.get() +
				'}';
	}
}
