package org.pharmgkb.parsers.vcf.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.pharmgkb.parsers.vcf.model.metadata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts a VCF metadata line (as a string) to the appropriate subclass of {@link VcfMetadata}/
 * @author Douglas Myers-Turnbull
 */
public class VcfMetadataFactory {

	private VcfMetadataFactory() {}

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final Pattern sf_pattern = Pattern.compile("##([^=]+)=<([^>]+)>");
	private static final Splitter sf_equalsSplitter = Splitter.on('=');
	private static final Splitter sf_tabSplitter = Splitter.on('\t');

	@Nonnull
	public static VcfMetadata translate(@Nonnull String line) {
		Preconditions.checkNotNull(line, "Metadata line cannot be null");
		Preconditions.checkArgument(line.startsWith("#"), "Metadata line does not start with #; was [[[" + line + "]]]");
		Matcher matcher = sf_pattern.matcher(line);
		if (matcher.matches()) {
			switch(matcher.group(1)) {
				// we can't pull the call to build() out without an exception if it's a raw metadata that
				// happens to start with ##xxx=<yyy>
				case VcfMetadataType.ALT_ID: return new VcfAltMetadata(build(matcher.group(2)));
				case VcfMetadataType.FILTER_ID: return new VcfFilterMetadata(build(matcher.group(2)));
				case VcfMetadataType.INFO_ID: return new VcfInfoMetadata(build(matcher.group(2)));
				case VcfMetadataType.FORMAT_ID: return new VcfFormatMetadata(build(matcher.group(2)));
				case VcfMetadataType.SAMPLE_ID: return new VcfSampleMetadata(build(matcher.group(2)));
				case VcfMetadataType.CONTIG_ID: return new VcfContigMetadata(build(matcher.group(2)));
				case VcfMetadataType.PEDIGREE_ID: return new VcfPedigreeMetadata(build(matcher.group(2)));
			}
		}
		if (line.startsWith("##fileformat=VCFv")) {
			String version = line.substring("##fileformat=VCFv".length());
			if (!version.equals("4.3")) {
				sf_logger.warn("This package is only guaranteed to work for VCF version 4.3; this version is {}", version);
			}
			return new VcfVersionMetadata(version);
		}
		if (line.startsWith("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT")) {
			List<String> list = sf_tabSplitter.splitToList(line);
			if (list.size() < 10) {
				return new VcfHeaderMetadata(Collections.emptyList());
			}
			return new VcfHeaderMetadata(list.subList(9, list.size()));
		}
		return new VcfRawMetadata(line);
	}

	@Nonnull
	private static Map<String, String> build(@Nonnull String props) {
		Map<String, String> map = new LinkedHashMap<>();
		for (String s : split(props, ',')) {
			Iterator<String> parts = sf_equalsSplitter.split(s).iterator();
			try {
				map.put(parts.next(), parts.next());
				if (parts.hasNext()) {
					throw new IllegalArgumentException("More than one equals sign in " + props);
				}
			} catch (NoSuchElementException e) {
				throw new IllegalArgumentException("Bad properties string: " + props, e);
			}
		}
		return map;
	}

	@Nonnull
	private static List<String> split(@Nonnull String input, char delimiter) {
		List<String> result = new ArrayList<>(input.length() + 1);
		int start = 0;
		boolean inQuotes = false;
		for (int current = 0; current < input.length(); current++) {
			if (input.charAt(current) == '\"') {
				inQuotes = !inQuotes;
			}
			boolean atLastChar = (current == input.length() - 1);
			if (atLastChar) {
				result.add(input.substring(start));
			} else if (input.charAt(current) == delimiter && !inQuotes) {
				result.add(input.substring(start, current));
				start = current + 1;
			}
		}
		return result;
	}

}
