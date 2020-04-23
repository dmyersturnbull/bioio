package org.pharmgkb.parsers.bgee;

import org.pharmgkb.parsers.WebResource;
import org.pharmgkb.parsers.model.CommonSpecies;
import org.pharmgkb.parsers.utils.IoUtils;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.BufferedReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.StringJoiner;

/**
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class BgeeResource implements WebResource {

	private final URL m_url;

	public BgeeResource(@Nonnull String url) {
		this.m_url = IoUtils.getUrl(url);
	}

	public static BgeeResource ofSpecies(@Nonnull CommonSpecies species) {
		return ofSpecies(species.getFormalName());
	}
	public static BgeeResource ofSpecies(@Nonnull String speciesFormalName) {
		//String url = "ftp://ftp.bgee.org/current/download/calls/expr_calls/" + species.getFormalName().replace(" ", "_") + "_expr_simple_development.tsv.gz";
		String url = "ftp://ftp.bgee.org/current/download/calls/expr_calls/Danio_rerio_expr_simple_development.tsv.gz";
		return new BgeeResource(url);
	}

	public URL getUrl() {
		return m_url;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", BgeeResource.class.getSimpleName() + "[", "]")
				.add("url=" + m_url)
				.toString();
	}
}
