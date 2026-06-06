package objective2270;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExamInfoParser {

    private static final Pattern FILENAME_PATTERN = Pattern.compile(
            "Exam(\\d+)(Fall|Fal|Spr)(\\d{2})Version([A-Z])",
            Pattern.CASE_INSENSITIVE
    );

    public static ExamInfo parse(String filename) {
        String basename = filename.replaceAll(".*/", "").replaceAll("\\.tex$", "");

        Matcher matcher = FILENAME_PATTERN.matcher(basename);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Filename does not match expected pattern: " + filename);
        }

        int examNumber = Integer.parseInt(matcher.group(1));
        Semester semester = matcher.group(2).toLowerCase().startsWith("spr") ? Semester.SPRING : Semester.FALL;
        int year = 2000 + Integer.parseInt(matcher.group(3));
        String versionLetter = matcher.group(4).toUpperCase();

        return new ExamInfo(filename, year, semester, examNumber, versionLetter);
    }
}
