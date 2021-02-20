package org.pharmgkb.parsers.fasta.model;

import com.google.common.base.Splitter;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class IlluminaSequenceId {

	private final String m_instrument;
	private final String m_runId;
	private final String m_flowcellId;
	private final String m_flowcellLane;
	private final String m_titleInFlowcellLane;
	private final int m_clusterXCoordinateInTile;
	private final int m_clusterYCoordinateInFile;
	private final byte m_pairMember;
	private final boolean m_readIsFiltered;
	private final int m_controlBits;
	private final String m_indexSequence;

	public IlluminaSequenceId(
			@Nonnull String instrument, @Nonnull String runId,
			@Nonnull String flowcellId, @Nonnull String flowcellLane, @Nonnull String titleInFlowcellLane,
			int clusterXCoordinateInTile, int clusterYCoordinateInFile,
			@Nonnegative byte pairMember,
			boolean readIsFiltered,
			@Nonnegative int controlBits,
			@Nonnull String indexSequence
	) {
		m_instrument = instrument;
		m_runId = runId;
		m_flowcellId = flowcellId;
		m_flowcellLane = flowcellLane;
		m_titleInFlowcellLane = titleInFlowcellLane;
		m_clusterXCoordinateInTile = clusterXCoordinateInTile;
		m_clusterYCoordinateInFile = clusterYCoordinateInFile;
		m_pairMember = pairMember;
		m_readIsFiltered = readIsFiltered;
		m_controlBits = controlBits;
		m_indexSequence = indexSequence;
	}

	@Nonnull
	public static IlluminaSequenceId parse(@Nonnull String header) {
		List<String> fields = Splitter.on(":")
				.splitToList(header).stream()
				.map(String::trim)
				.collect(Collectors.toList());
		if (!fields.get(8).equals("Y") && !fields.get(8).equals("N")) {
			throw new IllegalArgumentException("Value " + fields.get(8) + " must be Y or N");
		}
		return new IlluminaSequenceId(
				fields.get(0),
				fields.get(1),
				fields.get(2),
				fields.get(3),
				fields.get(4),
				Integer.parseInt(fields.get(5)),
				Integer.parseInt(fields.get(6)),
				Byte.parseByte(fields.get(7)),
				fields.get(8).equals("Y"),
				Integer.parseUnsignedInt(fields.get(9)),
				fields.get(10)
		);
	}
	// ex: @EAS139:136:FC706VJ:2:2104:15343:197393 1:N:18:

	/*
	EAS139	the unique instrument name
	136	the run id
	FC706VJ	the flowcell id
	2	flowcell lane
	2104	tile number within the flowcell lane
	15343	'x'-coordinate of the cluster within the tile
	197393	'y'-coordinate of the cluster within the tile
	1	the member of a pair, 1 or 2 (paired-end or mate-pair reads only)
	Y	Y if the read is filtered, N otherwise
	18	0 when none of the control bits are on, otherwise it is an even number
	ATCACG	index sequence
	 */
}
