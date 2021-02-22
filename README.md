# bioio

![stability-stable](https://img.shields.io/badge/stability-stable-green.svg)
![Active](https://img.shields.io/static/v1?label=development&message=active&color=green)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Latest release](https://img.shields.io/github/v/tag/dmyersturnbull/genomics-io)
![Java compatibility](https://img.shields.io/static/v1?label=Java&message=14%2b)
![Maven Central](https://img.shields.io/maven-central/v/dmyersturnbull/genomics-io)
![GitHub last commit](https://img.shields.io/github/last-commit/dmyersturnbull/genomics-io?color=green)

Efficient, high-quality streaming parsers and writers for 12 text-based formats used in bioinformatics.

The goal is to have the best possible parsers for the most problematic ancient formats.

**Supported formats:**
VCF, FASTA, GenBank, BED, GFF/GTV/GVF, UCSC chain,
pre-MAKEPED, BGEE, Turtle/RDF,
matrices/tables/CSV/TSV

**Features & choices:**

- Reads and writes Java 8+ Streams, keeping only essential metadata in memory.
- Parses every part of a format, leaving nothing as text unnecessarily.
- Has a consistent API. Coordinates are always 0-indexed and text is always escaped (according to specifications).
- Immutable, thread-safe, null-pointer-safe (`Optional<>`), and arbitrary-precision.

#### Example:

This example reads, filters, and writes a VCF file.

```java
import org.pharmgkb.parsers.vcf;

Stream<VcfPosition> goodMitochondrialCalls = new VcfDataParser().parseFile(path)
	.filter(p -> p.chromosome.isMitochondial())
	.filter(VcfFilters.qualityAtLeast(10)) // converts to BigDecimal

new VcfDataWriter().writeToFile(goodMitochondrialCalls, filteredPath);
```

## Build/install

Compatible with Java 14, 15, and 16.
You can get the artifacts from Maven Central.

#### Maven

```xml
<dependency>
    <groupId>com.pharmgkb.bioio</groupId>
    <artifactId>bioio</artifactId>
    <version>0.3.0</version>
</dependency>
```

#### Gradle

```groovy
implementation group: 'com.pharmgkb.bioio', name: 'bioio', version: '0.3.0'
```

#### SBT

```
"com.pharmgkb.bioio" % "bioio" % "0.3.0"
```

#### Pre-build JAR

[Releases](https://github.com/dmyersturnbull/genomics-io/releases) contain both _fat_ JARs (containing dependencies)
and _thin_ JARs (without dependencies), independently for each subproject
(e.g. `bioio-vcf` for VCF, or `bioio-gff` for GFF/GTV/GVF).

You can build artifacts from a source checkout using Gradle:

- To JAR all subprojects, run `gradle jarAll`
- To build a single subproject (e.g. VCF), run `gradle :vcf:jar`

## Package contents

### Formats

Formats listed in bold are currently implemented.

- Variant calls: **VCF**
- Gene features: **GenBank, BED, GFF3, GTF, GVF**
- Sequences: **FASTA**, EMBL, FASTA alignment, **FASTQ**, Seq, faidx (FASTQ indices)
- Expression: **BGEE**
- Coordinate mapping: **UCSC chain**
- Phylogenetics & pedigrees: **pre-MAKEPED**, LINKAGE, Nexus
- High-level: Swiss-Prot
- Raw reads: SAM
- Protein structure: PDB (non-comprehensive)
- RNA structure: Bpseq, Connect/CT, Vienna, Base-Paring, Dot-Bracket, Dot-Plot
- Other: cytoband
- Misc: Matrices/tables/CSV/TSV, alignment, **Turtle (and RDF)**

### Extra things

By accident, a few pieces of code may be generally useful:

- `org.pharmgkb.core.WebResource` (downloadable caching resource)
- `org.pharmgkb.core.utils.Try` (monadic attempt/recovery/mapping)
- `org.pharmgkb.core.utils.IoUtils` (text streams, gzip, and URLs)
- `org.pharmgkb.core.utils.HttpHeadResponse` (superior wrapper around incoming HTTP headers)
- `org.pharmgkb.core.utils.ReflectingConstructor` (quiet reflection to deal with type erasure)
- `org.pharmgkb.core.escape.*` (model for escaping and unescaping)
- `org.pharmgkb.core.model.GeneralizedBigDecimal` (BigDecimal that accepts NaN, Inf, and -Inf)

## Examples

This long list of examples showcases many of the parsers.
For added flavor, they also use various methods for IO (`parseAll`, etc.) and various Java 8+ `Stream`
functions (`parallel()`, `collect`, `flatMap`, etc.)

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

```java
Stream<BigDecimal> MatrixParser.tabs().parseAll(file).map(BigDecimal::new);
```

### Guiding principles

1. Where possible, a parser is a `Function<String, R>` or `Function<Stream<String>, R>`,
   and writer is a `Function<R, String>` or `Function<R, Stream<String>>`.
   [Java 8+ Streams](http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html)
   are expected to be used.
2. Null values are generally banned from public methods in favor of
   [`Optional`](https://download.java.net/java/early_access/jdk16/docs/api/java.base/java/util/Optional.html).
   See http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html for more information.
3. Most operations are thread-safe. Thread safety is annotated using `javax.annotation.concurrent`.
4. Top-level data classes are immutable, as annotated by or `javax.annotation.concurrent.Immutable`.
5. The builder pattern is used for non-trivial classes. Each builder has a copy constructor.
6. Links to specifications are provided. Any choice made in an ambiguous specification is documented.
7. Parsing and writing is _moderately_ strict. Severe violations throw a `BadDataFormatException`,
   and milder violations are logged as warnings using SLF4J.
   Not every aspect of a specification is validated.
8. For specification-mandated escape sequences, encoding and decoding is automatic.
9. Coordinates are _always 0-based_, even for 1-based formats.
   This is to ensure consistency as well as arithmetic simplicity.

### Pitfalls

1. Never reuse a parser for a new stream.
   Some parsers need to track some metadata on the stream.
   For example, the multiline FASTQ parser needs to know the length of the last sequence.
   (Otherwise, it’s impossible to know where a score ends and a new header begins!)

## License, authors, & contributing

Licensed under the [Mozilla Public License, version 2.0](https://www.mozilla.org/en-US/MPL/2.0/).

Copyright 2015–2021, the authors

Please refer to the [contributing guide](https://github.com/dmyersturnbull/bioio/blob/main/CONTRIBUTING.md).

**Credits:**

- Douglas Myers-Turnbull (design and parsers)
- Mark Woon (bug fixes and code review)
- the Stanford University School of Medicine
- the [Pharmacogenomics Knowledge Base](https://pharmgkb.org) at Stanford
- the University of California, San Francisco (UCSF)
- the [Keiser Lab](https://keiserlab.org) at UCSF
