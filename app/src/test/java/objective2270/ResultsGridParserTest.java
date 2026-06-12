package objective2270;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResultsGridParserTest {

    private static final String CSV = """
            Student Name,ID Number,Key Name,%,Score,# Correct,# Correct Less Essay,Essay,Blank Count,Multiple Count,Class ID,Batch Sequence #,Q 1,Q 2,Q 3
            Key A,,A,,106,36,36,0,,,,,AD,B,C
            "ABDULLAH, FAISAL",110067270,A,85.4%,90.5,30,30,0,0,12,0100,AAP0028,AD,B,
            """;

    private ResultsGrid grid;

    @BeforeEach
    void parseGrid() {
        grid = ResultsGridParser.parse(CSV);
    }

    @Test
    void parsesAnswerKeys() {
        assertEquals(
                List.of(
                        new AnswerKeyEntry("A", 1, "AD"),
                        new AnswerKeyEntry("A", 2, "B"),
                        new AnswerKeyEntry("A", 3, "C")
                ),
                grid.answerKeys()
        );
    }

    @Test
    void parsesStudentRecords() {
        assertEquals(
                List.of(new StudentRecord("110067270", "ABDULLAH, FAISAL", "A", 85.4, 90.5, 30)),
                grid.students()
        );
    }

    @Test
    void parsesStudentAnswers() {
        assertEquals(
                List.of(
                        new StudentAnswer("110067270", 1, "AD"),
                        new StudentAnswer("110067270", 2, "B"),
                        new StudentAnswer("110067270", 3, "")
                ),
                grid.studentAnswers()
        );
    }
}
