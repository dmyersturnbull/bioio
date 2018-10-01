package org.pharmgkb.parsers.bgee;


import org.pharmgkb.parsers.model.CommonSpecies;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringJoiner;
import java.util.zip.GZIPInputStream;

/**
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class BgeeResource {

	private final URL m_url;

	public BgeeResource(URL url) {
		this.m_url = url;
		System.out.println(this.m_url);
	}

	@Nonnull
	public BufferedReader newReader() throws IOException {
		return new BufferedReader((new InputStreamReader(new GZIPInputStream(m_url.openStream()))));
	}

	public static BgeeResource ofSpecies(CommonSpecies species) {
		return ofSpecies(species.getFormalName());
	}
	public static BgeeResource ofSpecies(String speciesFormalName) {
		//String url = "ftp://ftp.bgee.org/current/download/calls/expr_calls/" + species.getFormalName().replace(" ", "_") + "_expr_simple_development.tsv.gz";
		String url = "ftp://ftp.bgee.org/current/download/calls/expr_calls/Danio_rerio_expr_simple_development.tsv.gz";
		try {
			return new BgeeResource(new URL(url));
		} catch (MalformedURLException e) {
			throw new IllegalStateException("URL " + url + " somehow malformed");
		}
	}


	@Override
	public String toString() {
		return new StringJoiner(", ", BgeeResource.class.getSimpleName() + "[", "]")
				.add("url=" + m_url)
				.toString();
	}
}
