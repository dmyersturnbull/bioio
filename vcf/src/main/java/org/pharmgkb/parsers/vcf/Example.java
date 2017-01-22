package org.pharmgkb.parsers.vcf;

import org.pharmgkb.parsers.vcf.model.VcfMetadataCollection;
import org.pharmgkb.parsers.vcf.model.VcfPosition;
import org.pharmgkb.parsers.vcf.model.extra.ReservedFormatProperty;
import org.pharmgkb.parsers.vcf.model.extra.VcfGenotype;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Douglas Myers-Turnbull
 */
public class Example {

	public static void main(@Nonnull String... args) throws IOException {
		example1(Paths.get(args[0]), Paths.get(args[1]));
		if (args.length > 2) {
//			example2(Paths.get(args[2]), Paths.get(args[3]));
		}
	}

	public static void example1(@Nonnull Path input, @Nonnull Path output) throws IOException {

		VcfMetadataCollection metadata = new VcfMetadataParser().parse(input); // short-circuits during read
		Stream<VcfPosition> data = new VcfDataParser().parseAll(input)
				.filter(p -> p.getQuality().isPresent() && p.getQuality().get().greaterThan("10"))
				.map(p -> new VcfPosition.Builder(p).clearFilters().build())
				.peek(new VcfValidator.Builder(metadata).warnOnly().build()); // verify consistent with metadata

		new VcfMetadataWriter().writeToFile(metadata.getLines(), output);
		new VcfDataWriter().appendToFile(data, output);

	}
//
//	public static void example2(@Nonnull Path input, @Nonnull Path output) throws IOException {
//		VcfFileParser parser = new VcfFileParser(input);
//		Stream<VcfPosition> data = parser.parse()
//				.filter(p -> p.getQuality().isPresent() && p.getQuality().get().greaterThan("10"))
//				.map(p -> new VcfPosition.Builder(p).clearFilters().build())
//				.peek(new VcfValidator.Builder(parser.getMetadata()).warnOnly().build()); // verify consistent with metadata
//		new VcfFileWriter(output).writeToFile(parser.getMetadata(), data);
//	}

	public static void example3(@Nonnull Path input) throws IOException {
		Map<String, Long> genotypeCounts = new VcfDataParser().parseAll(input)
				.parallel()
				.flatMap(p -> p.getSamples().stream())
				.filter(s -> s.containsKey(ReservedFormatProperty.Genotype))
				.map(s -> s.get(ReservedFormatProperty.Genotype).get())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	public static void example4(@Nonnull Path input) throws IOException {
		Set<VcfGenotype> genotypes = new VcfDataParser().parseAll(input)
				.parallel().unordered() // unordered() improves efficiency of distinct()
				.flatMap(p -> p.getGenotypes().stream())
				.filter(Optional::isPresent)
				.map(Optional::get)
				.distinct()
				.collect(Collectors.toSet());
	}

}
