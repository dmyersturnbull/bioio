package org.pharmgkb.parsers.fasta.model;

import org.pharmgkb.parsers.BadDataFormatException;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public interface HasScores {

	@Nonnull
	String getScores();

	@Nonnull
	default Stream<Character> scoresAsStream() {
		return getScores().chars().mapToObj(i -> (char)i);
	}

	@Nonnull
	default Stream<Phred33Score> scoresToPhred33() throws BadDataFormatException {
		return scoresAsStream().map(Phred33Score::fromChar);
	}
}
