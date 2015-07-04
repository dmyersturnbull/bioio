# genome-sequence-io
Read and write from various bioinformatics sequence formats, currently BED, GFF2, GFF3, GTF, GVF, VCF, FASTA, chain (genome alignment), and pre-MAKEPED (pedigree).

The following principles are used:
  1. In general, a parser is a `Function<String, R>`, and writer is a `Function<R, String>`. [Java 8 Streams](http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html) should be used.
  2. Null values are banned from the return values of public methods in favor of [`Optional`](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html). See http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html for more information.
  3. All operations are thread-safe, as annotated by `javax.annotation.concurrent.ThreadSafe`.
  4. Top-level data classes are immutable, as annotated by  or `javax.annotation.concurrent.Immutable`.
  5. The builder pattern is used for non-trivial classes in favor of constructors or static factory methods.
  6. Format specifications are linked. In cases where the specification is ambiguous, the interpretation used is documented.
  7. Moderate strictness is used when parsing, and complete strictness is used when writing.