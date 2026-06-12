package objective2270;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResultsGridParserUnhappyPathTest {

    @Test
    void throwsWhenRequiredColumnMissing() {
        String csv = """
                Student Name,Key Name,%,Score,# Correct,Q 1
                Key A,A,,106,36,AD
                """;

        assertThrows(IllegalArgumentException.class, () -> ResultsGridParser.parse(csv));
    }

    @Test
    void returnsEmptyGridForHeaderOnly() {
        String csv = "Student Name,ID Number,Key Name,%,Score,# Correct,Q 1\n";

        ResultsGrid grid = ResultsGridParser.parse(csv);

        assertEquals(List.of(), grid.answerKeys());
        assertEquals(List.of(), grid.students());
        assertEquals(List.of(), grid.studentAnswers());
    }

    @Test
    void skipsBlankLines() {
        String csv = """
                Student Name,ID Number,Key Name,%,Score,# Correct,Q 1

                Key A,,A,,106,36,AD
                """;

        ResultsGrid grid = ResultsGridParser.parse(csv);

        assertEquals(List.of(new AnswerKeyEntry("A", 1, "AD")), grid.answerKeys());
    }

    @Test
    void warnsAndIgnoresRowsWithExtraColumns() {
        String csv = """
                Student Name,ID Number,Key Name,%,Score,# Correct,Q 1,Q 2
                Key A,,A,,106,36,AD,
                "ABDULLAH, FAISAL",110067270,A,85.4%,90.5,30,AD,B, some other text
                """;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        ResultsGrid grid;
        try {
            grid = ResultsGridParser.parse(csv);
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(out.toString().contains("ABDULLAH, FAISAL"), "Expected a warning naming the offending row");

        assertEquals(List.of(new AnswerKeyEntry("A", 1, "AD")), grid.answerKeys());

        assertEquals(
                List.of(
                        new StudentAnswer("110067270", 1, "AD"),
                        new StudentAnswer("110067270", 2, "B")
                ),
                grid.studentAnswers()
        );
    }

    @Test
    void throwsOnNonNumericScore() {
        String csv = """
                Student Name,ID Number,Key Name,%,Score,# Correct,Q 1
                "ABDULLAH, FAISAL",110067270,A,85.4%,N/A,30,AD
                """;

        assertThrows(NumberFormatException.class, () -> ResultsGridParser.parse(csv));
    }
}
