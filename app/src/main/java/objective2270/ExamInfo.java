package objective2270;

public record ExamInfo(
    String filename,
    int year,
    Semester semester,
    int examNumber,
    String versionLetter
) {}
