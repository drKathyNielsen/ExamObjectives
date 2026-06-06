package objective2270;

public record Question(
    int questionNumber,
    String objectiveText,
    String solution,
    String rawLatex,
    QuestionType questionType
) {}
