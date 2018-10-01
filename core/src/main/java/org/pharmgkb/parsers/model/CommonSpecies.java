package org.pharmgkb.parsers.model;

import javax.annotation.Nonnull;


public enum CommonSpecies {

	EColi("Escherichia coli", "E. coli"),
	YeastCerevisiae("Saccharomyces cerevisiae", "S. cerevisiae"),
	YeastPombe("Schizosaccharomyces pombe", "S. pombe"),
	BacillusSubtillis("Bacillus subtilis", "B. subtilis"),
	MycoplasmaGenitalium("Mycoplasma genitalium", "M. genitalium"),
	AlivibrioFischeri("Aliivibrio fischeri", "A. fischeri"),
	PlasmodiumFalciparum("Plasmodium falciparum", "P. falciparum"),
	Mustard("Arabidopsis thaliana", "wild mustard"),
	Rice("Oryza sativa", "rice"),
	Salmon("Salmo salar", "atlantic salmon"),
	HoneyBee("Apis mellifera", "honey bee"),
	Human("Homo sapiens", "human"),
	Mouse("Mus musculus", "mouse"),
	Zebrafish("Danio rerio", "zebrafish"),
	FruitFly("Drosophila melanogaster", "fruit fly"),
	CElegans("Caenorhabditis elegans", "nematode"),
	Chimpanzee("Pan troglodytes", "chimpanzee"),
	Bonobo("Pan paniscus", "bonobo"),
	Gorilla("Gorilla gorilla", "gorilla"),
	Macaque("Macaca mulatta", "macaque"),
	Rat("Rattus norvegicus", "brown rat"),
	Cow("Box taurus", "cow"),
	Pig("Sus scrofa", "pig"),
	Horse("Equus caballus", "horse"),
	Rabbit("Oryctolagus cuniculus", "rabbit"),
	Dog("Canis lupus familiaris", "dog"),
	Cat("Felis catus", "cat"),
	Chicken("Gallus gallus", "chiken")
	;

	private final String m_formalName;
	private final String m_commonName;

	CommonSpecies(String formalName, String commonName) {
		this.m_formalName = formalName;
		this.m_commonName = commonName;
	}

	@Nonnull
	public String getFormalName() {
		return m_formalName;
	}

	@Nonnull
	public String getCommonName() {
		return m_commonName;
	}
}
