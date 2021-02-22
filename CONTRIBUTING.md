# Contributing

Bioio is licensed under the
[Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
New issues and pull requests are welcome.
Feel free to direct a question to the authors by creating an [issue with the _question_ tag](https://github.com/dmyersturnbull/bioio/issues/new?assignees=&labels=kind%3A+question&template=question.md).
Contributors are asked to abide by both the [GitHub community guidelines](https://docs.github.com/en/github/site-policy/github-community-guidelines)
and the [Contributor Code of Conduct, version 2.0](https://www.contributor-covenant.org/version/2/0/code_of_conduct/).

#### Pull requests

Please update `CHANGELOG.md` with your changes and list your name in the contributors section of the readme.

#### Running tests

You can run tests with `gradle :vcf:test` and compile using `gradle :vcf:build`.
Note that the latter will only run tests for `vcf` and `core`.

#### Publishing a new version

1. Bump the version in `build.gradle`, following
   [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
2. Create a [new release](https://github.com/dmyersturnbull/bioio/releases/new)
   with both the name and tag set to something like `v1.4.13` (keep the _v_).
3. An hour later, check that the publishing
   [workflow](https://github.com/dmyersturnbull/bioio/actions) passes
   and that the version on Maven Central is updated.
