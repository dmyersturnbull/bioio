# VCF parser notes

VCF is a much more complex format than GFF or BED and so requires some additional explanation.
The specification implemented is https://samtools.github.io/hts-specs/VCFv4.3.pdf. VCF4.2 might work, as might formats matching alternative specifications, but that is not guaranteed.

## Data and metadata

The VCF format specification makes a distinction between three types of data:

- metadata: beginning with a ##, including the vcfVersion line
- header: beginning with a single #, between the metadata and data
- data: positions and samples, not beginning with a #

However, this parser considers the header to be metadata, so that there are only two types of data:

- metadata, beginning with at least one #
- data: not beginning with a #

A VCF data line is a [VcfPosition](./src/main/java/org/pharmgkb/parsers/vcf/model/VcfPosition.java). A VCF metdata line is a subclass of [VcfMetadata](./src/main/java/org/pharmgkb/parsers/vcf/model/metadata/VcfMetadata.java).
There are a few types worth explicit mention:
- [VcfVersionMetadata](./src/main/java/org/pharmgkb/parsers/vcf/model/metadata/VcfVersionMetadata.java), the `##vcfVersion=` line
- [VcfHeaderMetadata](./src/main/java/org/pharmgkb/parsers/vcf/model/metadata/VcfHeaderMetadata.java), the header line (`#CHROM...`)
- [VcfMapMetadata](./src/main/java/org/pharmgkb/parsers/vcf/model/metadata/VcfMapMetadata.java), a list of key–value pairs of the form `##ABC=<XYZ=???,ZZZ=???,...`
- [VcfIdMetadata](src/main/java/org/pharmgkb/parsers/vcf/model/metadata/VcfIdMetadata.java), a `VcfMapMetadata` that contains an `ID` for reference by name. The ID is unique per metadata type, and it is used to look up metadata in [VcfMetadataCollection](./src/main/java/org/pharmgkb/parsers/vcf/model/VcfMetadataCollection.java).
- [VcfRawMetadata](./src/main/java/org/pharmgkb/parsers/vcf/model/metadata/VcfRawMetadata.java), a metadata line that has no clear structure


## Package organization

- [org.pharmgkb.parsers.vcf](./src/main/java/org/pharmgkb/parsers/vcf/) contains the parsers and writers, and a [validator](./src/main/java/org/pharmgkb/parsers/vcf/VcfValidator.java).
- [org.pharmgkb.parsers.vcf.model](./src/main/java/org/pharmgkb/parsers/vcf/model/) contains high-level classes like [VcfPosition.java](./src/main/java/org/pharmgkb/parsers/vcf/model/VcfPosition.java), [src/main/java/org/pharmgkb/parsers/vcf/model/VcfSample.java](VcfSample), and [VcfMetadataCollection](./src/main/java/org/pharmgkb/parsers/vcf/model/VcfMetdataCollection.java)
- [org.pharmgkb.parsers.vcf.model.metadata](./src/main/java/org/pharmgkb/parsers/vcf/model/metadata/) contains the metadata subclasses
- [org.pharmgkb.parsers.vcf.model.allele](./src/main/java/org/pharmgkb/parsers/vcf/model/allele/) contains a model for `REF` and `ALT` alleles in VCF data lines
- [org.pharmgkb.parsers.vcf.model.extra](./src/main/java/org/pharmgkb/parsers/vcf/model/extra/) contains additional classes such as [VcfGenotype](./src/main/java/org/pharmgkb/parsers/vcf/model/extra/VcfGenotype.java) and [ReservedFormatProperty](./src/main/java/org/pharmgkb/parsers/vcf/model/ReservedFormatProperty.java)
- [org.pharmgkb.parsers.vcf.utils](./src/main/java/org/pharmgkb/parsers/vcf/utils/) contains utilities used by the VCF package, which should ordinarily not be used directly

## Readers and writers

Parsing and writing is done separately for data and metadata. The main classes are:
- [VcfDataParser](./src/main/java/org/pharmgkb/parsers/vcf/VcfDataParser.java)
- [VcfMetadataParser](./src/main/java/org/pharmgkb/parsers/vcf/VcfMetadataParser.java)
- [VcfDataWriter](./src/main/java/org/pharmgkb/parsers/vcf/VcfDataWriter.java)
- [VcfMetadataWriter](./src/main/java/org/pharmgkb/parsers/vcf/VcfMetadataWriter.java)
- [VcfValidator](./src/main/java/org/pharmgkb/parsers/vcf/VcfValidator.java), which performs optional validation requiring both data and metadata

There is also a convenience class available for writing VCF files [VcfFileWriter](./src/main/java/org/pharmgkb/parsers/vcf/VcfFileWriter.java).
