package objective2270;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvExporter {

    public static void export(Path examdropDir, Path outputFile) throws IOException {
        List<Path> texFiles = Files.list(examdropDir)
                .filter(p -> p.toString().endsWith(".tex"))
                .sorted()
                .toList();

        Spr26Parser parser = new Spr26Parser();

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile))) {
            writer.println("version,questionNumber,questionType,objective");

            for (Path texFile : texFiles) {
                ExamInfo examInfo = ExamInfoParser.parse(texFile.getFileName().toString());
                String latex = Files.readString(texFile);
                List<Question> questions = parser.parse(latex);

                for (Question q : questions) {
                    writer.printf("%s,%d,%s,%s%n",
                            examInfo.versionLetter(),
                            q.questionNumber(),
                            q.questionType(),
                            csvField(q.objectiveText())
                    );
                }
            }
        }

        System.out.println("Wrote " + outputFile.toAbsolutePath());
    }

    private static String csvField(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
