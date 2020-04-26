package org.pharmgkb.parsers.chain;

import org.pharmgkb.parsers.WebResource;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A chain file from GoldenPath at UCSC.
 */
@Immutable
public class ChainResource extends WebResource<ChainResource> {

	protected ChainResource(@Nonnull String url, @Nonnull Optional<Path> cachePath) {
		super(url, true, cachePath);
	}

	public static ChainResource of(@Nonnull String genome1, @Nonnull String genome2) {
		// http://hgdownload.cse.ucsc.edu/goldenpath/hg19/liftOver/hg19ToDasNov2.over.chain.gz
		String url = "http://hgdownload.cse.ucsc.edu/goldenpath/hg19/liftOver/$1To$2.over.chain.gz"
				.replace("$1", genome1).replace("$2", genome2);
		return new ChainResource(url, Optional.empty());
	}
}
