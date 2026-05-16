import { readFileSync } from "node:fs";

/**
 * Metadata about the Gradle project.
 */
type GradleMetadata = {
  /**
   * The version of the cxf-codegen-gradle plugin.
   */
  version: string;

  /**
   * The version of Apache CXF.
   */
  cxfVersion: string;

  /**
   * The version of SLF4J.
   */
  slf4jVersion: string;

  /**
   * The Gradle version of this build.
   */
  gradleVersion: string;
};

/**
 * Reads and parses the Gradle project metadata from the generated JSON file.
 *
 * @returns a {@link GradleMetadata} object containing version information for
 * the plugin and its dependencies.
 */
export default function (): GradleMetadata {
  const contents = readFileSync("build/gradle-project-metadata.json", "utf-8");
  const metadata: GradleMetadata = JSON.parse(contents);
  return Object.fromEntries(
      Object.entries(metadata).map(([k, v]) => [k, JSON.stringify(v)])
  ) as Record<keyof GradleMetadata, string>;
}
