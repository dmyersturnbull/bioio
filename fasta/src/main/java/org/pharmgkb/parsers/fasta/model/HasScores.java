package org.pharmgkb.parsers.fasta.model;

import org.pharmgkb.parsers.BadDataFormatException;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public interface HasScores {

	@Nonnull
	String getScores();

	@Nonnull
	default Stream<Character> scoresAsStream() {
		return getScores().chars().mapToObj(s -> (char)s);
	}

	@Nonnull
	default Stream<Phred33Score> scoresToPhred33() throws BadDataFormatException {
		return this.scoresAsStream().map(Phred33Score::fromChar);
	}
}
