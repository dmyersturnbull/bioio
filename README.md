# genome-sequence-io
Read and write from various bioinformatics sequence formats, currently BED, GFF3, GTF, GVF, VCF, FASTA, chain (genome alignment), and pre-MAKEPED (pedigree).

Also see https://github.com/PharmGKB/vcf-parser.

### Guiding principles
  1. Where possible, a parser is a `Function<String, R>`, and writer is a `Function<R, String>`. [Java 8 Streams](http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html) should be used.
  2. Null values are banned from public methods in favor of [`Optional`](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html). See http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html for more information.
  3. All operations are thread-safe, as annotated by `javax.annotation.concurrent.ThreadSafe`. Builder patterns are an exception.
  4. Top-level data classes are immutable, as annotated by  or `javax.annotation.concurrent.Immutable`.
  5. The builder pattern is used for non-trivial classes in favor of constructors or static factory methods.
  6. Format specifications are linked. In cases where the specification is ambiguous, the interpretation used is documented.
  7. Moderate strictness is used when parsing, and greater strictness is used when writing.
  
### Samples

```java
// Store GFF3 (or GVF, or GTF) features into a list
List<Gff3Feature> features = new Gff3Parser().collectAll(inputFile);
features.get(0).getType(); // the parser unescaped this string

// Now write the lines:
new Gff3Writer().writeToFile(outputFile); 
// The writer percent-encodes GFF3 fields as necessary
```

```java
// From a BED file, get a stream of distinct chromosome names that start with "chr", doing so in parallel
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
// "Lift over" coordinates using a UCSC chain file, filtering out those that couldn't be lifted over
GenomeChain chain = new GenomeChainParser().apply(Files.lines(hg19ToGrch38ChainFile));
List<Locus> liftedOver = lociList.parallelStream()
                                 .map(chain)
                                 .filter(Optional::isPresent)
                                 .collect(Collectors.toList());
// You can also use new GenomeChainParser().parse(hg19ToGrch38ChainFile)
```

```java
// Read FASTA bases with a buffered random-access reader
RandomAccessFastaStream stream = new RandomAccessFastaStream.Builder(file).setnCharsInBuffer(4096).build();
char base = stream.read("gene_1", 58523);
```
