package objective2270;

import java.util.List;

public record ResultsGrid(
    List<AnswerKeyEntry> answerKeys,
    List<StudentRecord> students,
    List<StudentAnswer> studentAnswers
) {}
