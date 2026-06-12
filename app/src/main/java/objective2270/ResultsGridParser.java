package objective2270;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ResultsGridParser {

    public static ResultsGrid parse(Path csvFile) throws IOException {
        return parse(Files.readString(csvFile));
    }

    public static ResultsGrid parse(String csvContent) {
        List<String> lines = csvContent.lines().toList();

        ColumnLayout layout = ColumnLayout.from(parseLine(lines.get(0)));

        List<AnswerKeyEntry> answerKeys = new ArrayList<>();
        List<StudentRecord> students = new ArrayList<>();
        List<StudentAnswer> studentAnswers = new ArrayList<>();

        for (String line : lines.subList(1, lines.size())) {
            if (line.isBlank()) {
                continue;
            }

            String[] fields = parseLine(line);

            if (isAnswerKeyRow(fields, layout)) {
                answerKeys.addAll(readAnswerKeyEntries(fields, layout));
            } else {
                students.add(readStudentRecord(fields, layout));
                studentAnswers.addAll(readStudentAnswers(fields, layout));
            }
        }

        return new ResultsGrid(answerKeys, students, studentAnswers);
    }

    private static boolean isAnswerKeyRow(String[] fields, ColumnLayout layout) {
        return fields[layout.idIndex()].trim().isEmpty();
    }

    private static List<AnswerKeyEntry> readAnswerKeyEntries(String[] fields, ColumnLayout layout) {
        String version = fields[layout.keyNameIndex()].trim();

        List<AnswerKeyEntry> entries = new ArrayList<>();
        for (QuestionColumn questionColumn : layout.questionColumns()) {
            String answer = questionColumn.read(fields);
            if (!answer.isEmpty()) {
                entries.add(new AnswerKeyEntry(version, questionColumn.questionNumber(), answer));
            }
        }
        return entries;
    }

    private static StudentRecord readStudentRecord(String[] fields, ColumnLayout layout) {
        return new StudentRecord(
                fields[layout.idIndex()].trim(),
                fields[layout.nameIndex()].trim(),
                fields[layout.keyNameIndex()].trim(),
                parsePercent(fields[layout.percentIndex()]),
                Double.parseDouble(fields[layout.scoreIndex()].trim()),
                Integer.parseInt(fields[layout.numCorrectIndex()].trim())
        );
    }

    private static List<StudentAnswer> readStudentAnswers(String[] fields, ColumnLayout layout) {
        String idNumber = fields[layout.idIndex()].trim();

        List<StudentAnswer> answers = new ArrayList<>();
        for (QuestionColumn questionColumn : layout.questionColumns()) {
            answers.add(new StudentAnswer(idNumber, questionColumn.questionNumber(), questionColumn.read(fields)));
        }
        return answers;
    }

    private static double parsePercent(String value) {
        String trimmed = value.trim();
        return trimmed.endsWith("%")
                ? Double.parseDouble(trimmed.substring(0, trimmed.length() - 1))
                : Double.parseDouble(trimmed);
    }

    private static String[] parseLine(String line) {
        return new CsvLineReader(line).fields();
    }

    private record QuestionColumn(int questionNumber, int columnIndex) {
        String read(String[] fields) {
            return fields[columnIndex].trim();
        }
    }

    private record ColumnLayout(
            int idIndex,
            int nameIndex,
            int keyNameIndex,
            int percentIndex,
            int scoreIndex,
            int numCorrectIndex,
            List<QuestionColumn> questionColumns
    ) {
        static ColumnLayout from(String[] header) {
            return new ColumnLayout(
                    indexOf(header, "ID Number"),
                    indexOf(header, "Student Name"),
                    indexOf(header, "Key Name"),
                    indexOf(header, "%"),
                    indexOf(header, "Score"),
                    indexOf(header, "# Correct"),
                    questionColumnsOf(header)
            );
        }

        private static int indexOf(String[] header, String name) {
            for (int i = 0; i < header.length; i++) {
                if (header[i].trim().equals(name)) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Column not found: " + name);
        }

        private static List<QuestionColumn> questionColumnsOf(String[] header) {
            List<QuestionColumn> columns = new ArrayList<>();
            for (int i = 0; i < header.length; i++) {
                String columnName = header[i].trim();
                if (columnName.matches("Q \\d+")) {
                    int questionNumber = Integer.parseInt(columnName.substring(2).trim());
                    columns.add(new QuestionColumn(questionNumber, i));
                }
            }
            return columns;
        }
    }

    private static class CsvLineReader {
        private final String line;
        private final List<String> fields = new ArrayList<>();
        private final StringBuilder current = new StringBuilder();
        private boolean inQuotes = false;

        CsvLineReader(String line) {
            this.line = line;
            parse();
        }

        private void parse() {
            for (int i = 0; i < line.length(); i++) {
                i = consumeChar(i);
            }
            fields.add(current.toString());
        }

        private int consumeChar(int i) {
            char c = line.charAt(i);

            if (inQuotes) {
                return consumeQuotedChar(i, c);
            }
            if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                endField();
            } else {
                current.append(c);
            }
            return i;
        }

        private int consumeQuotedChar(int i, char c) {
            if (c != '"') {
                current.append(c);
                return i;
            }
            boolean isEscapedQuote = i + 1 < line.length() && line.charAt(i + 1) == '"';
            if (isEscapedQuote) {
                current.append('"');
                return i + 1;
            }
            inQuotes = false;
            return i;
        }

        private void endField() {
            fields.add(current.toString());
            current.setLength(0);
        }

        String[] fields() {
            return fields.toArray(new String[0]);
        }
    }
}
