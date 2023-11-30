import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

class IsSnapshot {

    int run() {
        var gradleProperties = new Properties();
        try {
            gradleProperties.load(Files.newInputStream(Path.of("gradle.properties")));
        } catch (IOException ex) {
            System.err.println("Unable to read gradle.properties");
            return 1;
        }
        var version = gradleProperties.get("version").toString().toLowerCase(Locale.ENGLISH);
        System.err.println("Version from Gradle properties file: " + version);
        if (!version.contains("snapshot")) {
            System.err.println("Expected version to be a snapshot");
            return 1;
        }
        return 0;
    }

}
