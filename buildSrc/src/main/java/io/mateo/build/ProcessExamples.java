package io.mateo.build;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Task to processes example source files by extracting the content between fold markers and writing it to the output
 * directory.
 */
@CacheableTask
public abstract class ProcessExamples extends SourceTask {

    private static final Pattern START_FOLD = Pattern.compile("^\\s*//\\s*#region.*");

    private static final Pattern END_FOLD = Pattern.compile("^\\s*//\\s*#endregion.*");

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void process() {
        getSource()
                .getFiles().stream()
                .map(File::toPath)
                .forEach(example -> {
                    PrintWriter writer = createWriter(example);
                    try (BufferedReader reader = Files.newBufferedReader(example)) {
                        String currentLine;
                        boolean shouldWrite = false;
                        while ((currentLine = reader.readLine()) != null) {
                            if (END_FOLD.matcher(currentLine).matches()) {
                                shouldWrite = false;
                                continue;
                            }
                            if (START_FOLD.matcher(currentLine).matches()) {
                                shouldWrite = true;
                                continue;
                            }
                            if (shouldWrite) {
                                writer.println(currentLine);
                            }
                        }
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }

                    writer.close();
                });
    }

    private PrintWriter createWriter(Path example) {
        Path parentName = example.getParent().getFileName();
        Path fileName = parentName.resolve(example.getFileName());
        Path out = getOutputDirectory().get().getAsFile().toPath().resolve(fileName.toString());
        try {
            Files.deleteIfExists(out);
            Files.createDirectories(out.getParent());
            Files.createFile(out);
            return new PrintWriter(Files.newBufferedWriter(out));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
