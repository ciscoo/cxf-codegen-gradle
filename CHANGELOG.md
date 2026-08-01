# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

> [!TIP]
> For a complete list of all _closed_ issues and pull requests for this release, consult the milestone of the release.

## Unreleased

## [3.0.0](https://github.com/ciscoo/cxf-codegen-gradle/milestone/18?closed=1) - 2026-07-31

- Overhaul documentation with VitePress.
- Build against Gradle 9.6.1.
- Introduce code generation through the Worker API.
- Upgrade to Apache CXF 4.2.1
- Guard against missing Java plugin when adding generated code to `main` source set.

## [2.5.0](https://github.com/ciscoo/cxf-codegen-gradle/milestone/18?closed=1) - 2025-09-25

> [!CAUTION]
> This will be the last release for 2.x. The next release will be 3.0.0 and will require Java 17 as the minimum to run
since Gradle itself requires Java 17 for Gradle 9.x which the plugin will be based on.

- Added formal Changelog
- Documentation samples now use Kotlin property assignment.
- Dropped versioned documentation
- Upgrade to Apache CXF 4.1.3
- Upgrade to SLF4J 2.0.17
- Test against Gradle 8.13
- Test against Gradle 8.14
- Test against Gradle 9.0.0
- Test against Gradle 9.1.0
