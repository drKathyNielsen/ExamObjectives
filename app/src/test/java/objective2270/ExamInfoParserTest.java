package objective2270;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExamInfoParserTest {

    @Test
    void parsesSpringExam() {
        ExamInfo info = ExamInfoParser.parse("Exam4Spr26VersionA100.tex");

        assertEquals(4, info.examNumber());
        assertEquals(Semester.SPRING, info.semester());
        assertEquals(2026, info.year());
        assertEquals("A", info.versionLetter());
        assertEquals("Exam4Spr26VersionA100.tex", info.filename());
    }

    @Test
    void parsesFallExam() {
        ExamInfo info = ExamInfoParser.parse("Exam2Fall25VersionB.tex");

        assertEquals(2, info.examNumber());
        assertEquals(Semester.FALL, info.semester());
        assertEquals(2025, info.year());
        assertEquals("B", info.versionLetter());
    }

    @Test
    void parsesFullPath() {
        ExamInfo info = ExamInfoParser.parse("/some/path/Exam1Spr26VersionC.tex");

        assertEquals(1, info.examNumber());
        assertEquals(Semester.SPRING, info.semester());
        assertEquals(2026, info.year());
        assertEquals("C", info.versionLetter());
        assertEquals("/some/path/Exam1Spr26VersionC.tex", info.filename());
    }

    @Test
    void throwsOnUnrecognizedFilename() {
        assertThrows(IllegalArgumentException.class, () ->
                ExamInfoParser.parse("midterm_spring2026.tex"));
    }
}
