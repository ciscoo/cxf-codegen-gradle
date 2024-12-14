package io.mateo.build;

import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.TypeSpec;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;

public abstract class GenerateVersionAccessor extends DefaultTask {

    @Inject
    public GenerateVersionAccessor(ProjectLayout layout) {
        getOutputDirectory().convention(layout.getBuildDirectory().dir("cxf-codegen-plugin-generated-sources"));
    }

    @Input
    public abstract Property<String> getCxfVersion();

    @Input
    public abstract Property<String> getSlf4jVersion();

    @Optional
    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void generate() throws IOException {
        var cxfVersionField = FieldSpec.builder(String.class, "CXF_VERSION")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", getCxfVersion().get())
                .build();

        var slf4jVersionField = FieldSpec.builder(String.class, "SLF4J_VERSION")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", getSlf4jVersion().get())
                .build();

        var generateVersionAccessorType = TypeSpec.classBuilder("GeneratedVersionAccessor")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("THIS CLASS WAS AUTO GENERATED, DO NOT EDIT")
                .addFields(List.of(cxfVersionField, slf4jVersionField))
                .build();

        var generateVersionAccessorJavaFile = JavaFile.builder("io.mateo.cxf.codegen.internal", generateVersionAccessorType)
                .build();

        generateVersionAccessorJavaFile.writeTo(getOutputDirectory().get().getAsFile().toPath());
    }

}
