package org.pharmgkb.parsers.bgee;

import org.pharmgkb.parsers.WebResource;
import org.pharmgkb.parsers.model.CommonSpecies;
import org.pharmgkb.parsers.utils.IoUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.BufferedReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class BgeeResource extends WebResource<BgeeResource> {

	protected BgeeResource(@Nonnull String url, @Nullable Optional<Path> cachePath) {
		super(url, true, cachePath);
	}

	@Nonnull
	public static BgeeResource ofSpecies(@Nonnull CommonSpecies species) {
		return ofSpecies(species.getFormalName());
	}

	@Nonnull
	public static BgeeResource ofSpecies(@Nonnull String speciesFormalName) {
		//String url = "ftp://ftp.bgee.org/current/download/calls/expr_calls/" + species.getFormalName().replace(" ", "_") + "_expr_simple_development.tsv.gz";
		String url = "ftp://ftp.bgee.org/current/download/calls/expr_calls/Danio_rerio_expr_simple_development.tsv.gz";
		return new BgeeResource(url, Optional.empty());
	}

}
