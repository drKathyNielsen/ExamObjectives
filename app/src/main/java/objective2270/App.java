package objective2270;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws Exception {
        Path examdropDir = Paths.get("examdrop");
        Path outputFile = Paths.get("outputfiles/questionObjectives.csv");
        CsvExporter.export(examdropDir, outputFile);
    }
}
