package objective2270;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spr26Parser  implements Parser {

    private static final Pattern QTAG_PATTERN =
            Pattern.compile("\\\\qtag\\{(.*)}");

    @Override
    public List<Question> parse(String latex) {
        List<Question> questions = new ArrayList<>();

        String[] lines = latex.split("\\R");

        int enumerateDepth = 0;
        int itemizeDepth = 0;
        int questionNumber = 0;

        boolean insideQuestion = false;

        String objective = null;
        StringBuilder rawLatex = new StringBuilder();

        boolean insideSolution = false;
        StringBuilder solution = new StringBuilder();

        List<String> answerChoices = new ArrayList<>();

        for (String line : lines) {

            String trimmed = line.trim();

            if (trimmed.startsWith("\\begin{enumerate")) {
                enumerateDepth++;
            }

            if (trimmed.startsWith("\\begin{itemize")) {
                itemizeDepth++;
            }

            if (insideQuestion) {
                rawLatex.append(line).append(System.lineSeparator());
            }

            Matcher qtagMatcher = QTAG_PATTERN.matcher(trimmed);

            if (qtagMatcher.matches()) {

                insideQuestion = true;

                rawLatex.setLength(0);
                rawLatex.append(line).append(System.lineSeparator());

                solution.setLength(0);
                answerChoices.clear();

                objective = normalizeObjective(qtagMatcher.group(1));

                continue;
            }

            if (!insideQuestion) {

                if (trimmed.startsWith("\\end{enumerate")) {
                    enumerateDepth--;
                }

                if (trimmed.startsWith("\\end{itemize")) {
                    itemizeDepth--;
                }

                continue;
            }

            if (trimmed.equals("\\begin{solution}")) {
                insideSolution = true;
                continue;
            }

            if (trimmed.equals("\\end{solution}")) {

                insideSolution = false;

                questions.add(
                        new Question(
                                questionNumber,
                                objective,
                                normalizeSolution(solution.toString()),
                                rawLatex.toString().trim(),
                                detectQuestionType(rawLatex.toString(), answerChoices)
                        )
                );

                insideQuestion = false;
                objective = null;
                answerChoices.clear();

                continue;
            }

            if (insideSolution) {
                solution.append(line).append(System.lineSeparator());
            }

            if (!insideSolution && enumerateDepth == 2 && trimmed.startsWith("\\item")) {
                answerChoices.add(trimmed.substring("\\item".length()).trim());
            }

            if (trimmed.startsWith("\\item") && enumerateDepth == 1 && itemizeDepth == 0) {
                questionNumber++;
            }

            if (trimmed.startsWith("\\end{enumerate")) {
                enumerateDepth--;
            }

            if (trimmed.startsWith("\\end{itemize")) {
                itemizeDepth--;
            }
        }

        return questions;
    }

    private QuestionType detectQuestionType(String rawLatex, List<String> answerChoices) {
        if (answerChoices.size() == 2) {
            String first = answerChoices.get(0).toLowerCase();
            String second = answerChoices.get(1).toLowerCase();
            if (first.equals("true") && second.equals("false")) {
                return QuestionType.TRUE_FALSE;
            }
        }

        String lower = rawLatex.toLowerCase();
        if (lower.contains("select all that apply")) {
            return QuestionType.SELECT_ALL;
        }
        if (lower.contains("select one") || lower.contains("choose one")) {
            return QuestionType.SELECT_ONE;
        }

        return QuestionType.SELECT_ONE;
    }

    private String normalizeObjective(String objective) {

        String normalized = objective;

        normalized = normalized.replaceFirst("^Objective\\s*:\\s*", "");
        normalized = normalized.trim();
        normalized = normalized.replaceAll("\\s+", " ");

        return normalized;
    }

    private String normalizeSolution(String solution) {
        return solution.strip();
    }
}