# genomics-io

![stability-stable](https://img.shields.io/badge/stability-stable-green.svg)
![Active](https://img.shields.io/static/v1?label=development&message=active&color=green)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Latest release](https://img.shields.io/github/v/tag/dmyersturnbull/genome-sequence-io)
![Test coverage](https://img.shields.io/codecov/c/github/dmyersturnbull/genome-sequence-io?token=thisisatoken)
[![Travis build](https://travis-ci.org/dmyersturnbull/genome-sequence-io.svg?branch=master)](https://travis-ci.org/dmyersturnbull/genome-sequence-io)


Efficient, high-quality streaming parsers and writers for 9 (soon 30) text-based formats used in bioinformatics.

Currently undergoing partial redesign as more formats are added.
Previous versions are stable, well-tested, and used in production.

Currently supported formats:
VCF, FASTA, GenBank, BED, GFF/GTV/GVF, UCSC chain,
pre-MAKEPED, BGEE, Turtle/RDF


Features & choices:
- Reads and writes Java 8+ Streams, keeping only essential metadata in memory.
- Parses every part of a format, leaving nothing as text unnecessarily.
- Has consistent API. Coordinates are always 0-indexed and text is always escaped (according to specifications).
- Immutable, thread-safe, null-pointer-safe (`Optional<>`), and uses only arbitrary-precision numbers.

This repository is a fork of [PharmGKB/genome-sequence-io](https://github.com/PharmGKB/genome-sequence-io) that adds VCF, GenBank, PDB, faidx, and Turtle parsers.


#### What it looks like:
This example reads, filters, and writes a VCF file.

```java
import org.pharmgkb.parsers.vcf;
Stream<VcfPosition> goodMitochondrialCalls = new VcfDataParser().parseFile(path)
	.filter(p -> p.chromosome.isMitochondial())
	.filter(VcfFilters.qualityAtLeast(10)) // converts to BigDecimal
new VcfDataWriter().writeToFile(goodMitochondrialCalls, filteredPath);
```

### Build/install

The project is not currently on Maven Central but should be soon.
Until then, you can download a [release](https://github.com/dmyersturnbull/genome-sequence-io/releases), which includes a JAR.

Alternatively, you can use Gradle to build it.
To JAR all subprojects, run `gradle jarAll`.
To build a single subproject, run `gradle :xxx:jar`, where `xxx` is the name of the subproject (for example, `gradle :gff:jar`).
You can also run tests with `gradle :xxx:test` and compile (without JARing) using `gradle :xxx:build`.
Note that running `gradle :xxx:gff` will only run tests for `gff` and `core`.


### Planned formats

- Variant calls: **VCF**
- Gene features: **GenBank, BED, GFF3, GTF, GVF**
- Sequences: **FASTA**, EMBL, FASTA alignment, FASTQ, Seq, faidx (FASTQ indices)
- Expression: **BGEE**
- Coordinate mapping: **UCSC chain**
- Phylogenetics & pedigrees: **pre-MAKEPED**, LINKAGE, Nexus
- High-level: Swiss-Prot
- Raw reads: SAM
- Protein structure: PDB (non-comprehensive)
- RNA structure: Bpseq, Connect/CT, Vienna, Base-Paring, Dot-Bracket, Dot-Plot
- Other: cytoband
- Misc: Matrices, CSV/TSV, alignment, **Turtle (and RDF)**


### Examples

This long list of examples showcases many of the parsers.
For added flavor, they also use various methods for IO (`parseAll`, etc.) and various Java 8+ `Stream` functions (`parallel()`, `collect`, `flatMap`, etc.)

```java
// Store GFF3 (or GVF, or GTF) features into a list
List<Gff3Feature> features = new Gff3Parser().collectAll(inputFile);
features.get(0).getType(); // the parser unescaped this string

// Now write the lines:
new Gff3Writer().writeToFile(outputFile); 
// The writer percent-encodes GFF3 fields as necessary
```

```java
// From a BED file, get distinct chromosome names that start with "chr", in parallel
Files.lines(file).map(new BedParser())
	.parallel()
	.map(BedFeature::getChromosome).distinct()
	.filter(chr -> chr.startsWith("chr"))
// You can also use new BedParser().parseAll(file)
```

```java
// From a pre-MAKEPED file, who are Harry Johnson's children?
Pedigree pedigree = new PedigreeParser.Builder().build().apply(Files.lines(file));
NavigableSet<Individual> children = pedigree.getFamily("Johnsons")
	.find("Harry Johnson")
	.getChildren();
```

```java
// Traverse through a family pedigree in topological order
Pedigree pedigree = new PedigreeParser.Builder().build().apply(Files.lines(file));
Stream<Individual> = pedigree.getFamily("Johnsons")
	.topologicalOrderStream();
```

```java
// "Lift over" coordinates using a UCSC chain file
// Filter out those that couldn't be lifted over
GenomeChain chain = new GenomeChainParser().apply(Files.lines(hg19ToGrch38ChainFile));
List<Locus> liftedOver = lociList.parallelStream()
	.map(chain)
	.filter(Optional::isPresent)
	.collect(Collectors.toList());
// You can also use new GenomeChainParser().parse(hg19ToGrch38ChainFile)
```

```java
// Print formal species names from a GenBank file
Path input = Paths.get("plasmid.genbank");
properties = new GenbankParser().parseAll(input)
	.filter(record -> record instanceof SourceAnnotation)
	.map(record -> record.getFormalName())
	.forEach(System.out::println)
```

```java
// Parse a GenBank file
// Get the set of "color" properties of features on the complement starting before the sequence
Set<String> properties = new GenbankParser().parseAll(input)
	.filter(record -> record instanceof FeaturesAnnotation)
	.flatMap(record -> record.getFeatures())
	.filter(feature -> record.range.isComplement());
	.filter(feature -> record.range.start() < 0);
	.flatMap(feature -> feature.getProperties().entrySet().stream())
	.filter(prop -> prop.getKey().equals("color"))
	.map(prop -> prop.getValue())
	.collect(Collectors.toSet())
```

```java
// Parse a GenBank file
// Get the set of "color" properties of features on the complement starting before the sequence
Path input = Paths.get("plasmid.genbank");
Set<String> properties = new GenbankParser().parseAll(input)
	.filter(record -> record instanceof FeaturesAnnotation)
	.flatMap(record -> record.getFeatures())
	.filter(feature -> record.range.isComplement());
	.filter(feature -> record.range.start() < 0);
	.flatMap(feature -> feature.getProperties().entrySet().stream())
	.filter(prop -> prop.getKey().equals("color"))
	.map(prop -> prop.getValue())
	.collect(Collectors.toSet())
```

```java
// Read FASTA bases with a buffered random-access reader
RandomAccessFastaStream stream = new RandomAccessFastaStream.Builder(file)
	.setnCharsInBuffer(4096)
	.build();
char base = stream.read("gene_1", 58523);
```

```java
// Suppose you have a 2GB FASTA file and a method smithWaterman that returns AlignmentResults
// Align each sequence and get the top 10 results, in parallel
MultilineFastaSequenceParser parser = new MultilineFastaSequenceParser.Builder().build();
List<AlignmentResult> topScores = parser.parseAll(Files.lines(fastaFile))
	.parallel()
	.peek(sequence -> logger.info("Aligning {}", sequence.getHeader())
	.map(sequence -> smithWaterman(sequence.getSequence(), reference))
	.sorted() // assuming AlignmentResult implements Comparable
	.limit(10);
}
```

```java
// Stream Triples in Turtle format from a URL
/*
@prefix myPrefix: <http://abc#owner> .
<http://abc#cat> "belongsTo" @myPrefix ;
	"hasSynonym" <http://abc#feline> .
 */
Stream<String> input = null;
try (BufferedReader reader = new BufferedReader(new InputStreamReader((HttpURLConnection) myUrl.openConnection()).getInputStream()))) {
	input = reader.lines();
}
TripleParser parser = new TripleParser(true);  // usePrefixes=true will replace prefixes
Stream<Triple> stream = input.map(new TripleParser());
// contains:  List[ http://abc#cat belongsTo http://abc#owner , http://abc#cat hasSynonym http://abc#feline ]
List<Prefix> prefixes = parser.getPrefixes();
```

```java
// Parse VCF, validate it, and write a new VCF file containing only positions whose QUAL field
// is at least 10, each with its FILTER field cleared
VcfMetadataCollection metadata = new VcfMetadataParser().parse(input); // short-circuits during read
Stream<VcfPosition> data = new VcfDataParser().parseAll(input)
	.filter(p -> p.getQuality().isPresent() && p.getQuality().get().greaterThanOrEqual("10"))
	.map(p -> new VcfPosition.Builder(p).clearFilters().build())
	.peek(new VcfValidator.Builder(metadata).warnOnly().build()); // verify consistent with metadata
new VcfMetadataWriter().writeToFile(metadata.getLines(), output);
new VcfDataWriter().appendToFile(data, output);
```


```java
// From a VCF file, associate every GT with its number of occurrences, in parallel
Map<String, Long> genotypeCounts = new VcfDataParser().parseAll(input)
	.parallel()
	.flatMap(p -> p.getSamples().stream())
	.filter(s -> s.containsKey(ReservedFormatProperty.Genotype))
	.map(s -> s.get(ReservedFormatProperty.Genotype).get())
	.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
```

### Guiding principles
  1. Where possible, a parser is a `Function<String, R>` or `Function<Stream<String>, R>`, and writer is a `Function<R, String>` or  `Function<R, Stream<String>>`. [Java 8 Streams](http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html) are therefore expected to be used.
  2. Null values are generally banned from public methods in favor of [`Optional`](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html). See http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html for more information.
  3. Most operations are thread-safe. Thread safety is annotated using `javax.annotation.concurrent`.
  4. Top-level data classes are immutable, as annotated by  or `javax.annotation.concurrent.Immutable`.
  5. The builder pattern is used for non-trivial classes. Each builder has a copy constructor.
  6. Links to specifications are provided. Any interpretation used for an ambiguous specification is documented.
  7. Parsing and writing is _moderately_ strict. Severe violations throw a `BadDataFormatException`, and milder violations are logged as warnings using SLF4J. Not every aspect of a specification is validated.
  8. For specification-mandated escape sequences, encoding and decoding is automatic.
  9. Coordinates are _always 0-based_, even for 1-based formats. This is to ensure consistency as well as arithmetic simplicity.

![Java compatibility](https://img.shields.io/static/v1?label=Java&message=14%2b)
![Maven Central](https://img.shields.io/maven-central/v/dmyersturnbull/genome-sequence-io)
[![Documentation](https://readthedocs.org/projects/genome-sequence-io/badge/?version=latest&style=flat-square)](https://readthedocs.org/projects/genome-sequence-io)
![GitHub last commit](https://img.shields.io/github/last-commit/dmyersturnbull/genome-sequence-io?color=green)
