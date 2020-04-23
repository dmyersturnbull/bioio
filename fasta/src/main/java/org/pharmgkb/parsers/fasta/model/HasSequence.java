package org.pharmgkb.parsers.fasta.model;

import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.model.AminoAcidCode;
import org.pharmgkb.parsers.model.NucleotideCode;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public interface HasSequence {

	@Nonnull
	String getHeader();

	@Nonnull
	String getSequence();

	@Nonnull
	default IlluminaSequenceId headerToIlluminaId() throws BadDataFormatException {
		return IlluminaSequenceId.parse(this.getHeader());
	}

	@Nonnull
	default Stream<Character> sequenceAsStream() {
		return this.getSequence().chars().mapToObj(s -> (char)s);
	}

	@Nonnull
	default Stream<NucleotideCode> sequenceToNucleotides() throws BadDataFormatException {
		return this.sequenceAsStream().map(NucleotideCode::fromChar);
	}

	@Nonnull
	default Stream<AminoAcidCode> sequenceToAminoAcids() throws BadDataFormatException {
		return this.sequenceAsStream().map(AminoAcidCode::fromChar);
	}


}
