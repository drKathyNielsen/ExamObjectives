package objective2270;

public record StudentRecord(
    String idNumber,
    String name,
    String examVersion,
    double percent,
    double score,
    int numCorrect
) {}
