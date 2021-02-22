# Changelog

Adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html)
and [keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [0.4.0] - unreleased

### Added

- BGEE module
- GenBank module
- Turtle module
- Text module
- FASTA writers
- FastQ parsers

### Changed

- Modernized build and project structure
- Travis → GitHub Actions
- _genome-sequence-io_ → _bioio_
- Bumped dependency versions

### Removed

- Support for Java < 14

### Fixed

- Removed IntelliJ files
- Dropped ProtonPack dependency
- Switched to JUnit 5

## [0.3.0] - 2017-01-21

### Added

- VCF module
- Multi-line FASTA parsers

## [0.2.0] - 2016-07-14

### Changed

- Moved `Locus`, `LocusRange`, and `ChromosomeName` to `core`
- Moved escaping code to an `escape` package under `core`
- Bumped dependency versions

## [0.1.0] - 2015-07-03

### Added

- Project structure
- BED module
- Chain module
- FASTA module
- GFF module
- Pedigree module
