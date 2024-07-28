import * as fs from "fs";

type GradleMetadata = {
    version: string;
    gradleVersion: string;
    cxfVersion: string;
};

export function readGradleMetadata(): GradleMetadata {
    const contents = fs.readFileSync(
        "build/gradle-project-metadata.json",
        "utf-8"
    );
    return JSON.parse(contents);
}
