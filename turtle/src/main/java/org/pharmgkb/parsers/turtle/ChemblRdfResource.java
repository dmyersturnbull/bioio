package org.pharmgkb.parsers.turtle;

import org.pharmgkb.parsers.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Immutable
public class ChemblRdfResource extends WebResource<ChemblRdfResource> {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final Pattern versionPattern = Pattern.compile("([0-9]{2})\\.([0-9])");
	public static final Set<String> KNOWN_VERSIONS = Set.of(
			"16.0", "17.0", "18.0", "19.0", "20.0", "21.0", "22.0", "22.1",  "23.0", "24.0", "24.1", "25.0", "26.0"
	);
	public static final Set<String> KNOWN_TYPES = Set.of(
			"activity", "assay", "bindingsite", "biocmpt", "cellline", "complextarget_targetcmpt_ls",
			"document", "grouptarget_targetcmpt_ls", "indication", "journal", "moa", "molecule",
			"molecule_chebi_ls", "molhierarchy", "protclass", "singletarget_targetcmpt_ls",
			"source", "target", "targetcmpt", "targetcmpt_uniprot_ls", "targetrel", "unichem"
	);

	protected ChemblRdfResource(@Nonnull String url, @Nonnull Optional<Path> cachePath) {
		super(url, true, cachePath);
	}

	public ChemblRdfResource of(@Nonnull String type, @Nonnull String version) {
		Matcher m = versionPattern.matcher(version);
		if (!m.matches() || (Integer.parseInt(m.group(1)) < 26 && !ChemblRdfResource.KNOWN_VERSIONS.contains(version))) {
			sf_logger.warn("Probable invalid ChEMBL RDF version # " + version);
		}
		// TODO damn, this is FTP!
		String url = "http://ftp.ebi.ac.uk/pub/databases/chembl/ChEMBL-RDF/$1/chembl_$1_$2.ttl.gz"
				.replace("$1", version).replace("$2", type);
		return new ChemblRdfResource(url, Optional.empty());
	}

}
