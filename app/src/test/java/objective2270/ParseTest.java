package objective2270;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParseTest {
    objective2270.Spr26Parser parser = new Spr26Parser();
    
    @Test
    void parsesSingleQuestion() {
        String latex = """
            \\begin{enumerate}

            \\qtag{Objective:  Can identify properties of heap and stack memory. }
            \\item Which of the following are True about stack memory?

            \\begin{enumerate}[label=\\Alph*.]
                \\item Local variables
                \\item Long lived variables
            \\end{enumerate}

            \\begin{solution}
            A
            \\end{solution}

            \\end{enumerate}
            """;

        List<Question> questions = parser.parse(latex);

        assertEquals(questions.size(), 1);

        Question question = questions.get(0);

        assertEquals(question.questionNumber(), 1);

        assertEquals(question.objectiveText(), "Can identify properties of heap and stack memory.");

        assertEquals(question.solution(), "A");
    }

    @Test
    void detectsTrueFalse() {
        String latex = """
            \\begin{enumerate}

            \\qtag{Objective: Test}
            \\item A browser's back button history behaves like a LIFO structure.
            \\begin{enumerate}[label=\\Alph*.]
                \\item True
                \\item False
            \\end{enumerate}
            \\begin{solution}
            A
            \\end{solution}

            \\end{enumerate}
            """;

        List<Question> questions = parser.parse(latex);

        assertEquals(QuestionType.TRUE_FALSE, questions.get(0).questionType());
    }

    @Test
    void detectsSelectAll() {
        String latex = """
            \\begin{enumerate}

            \\qtag{Objective: Test}
            \\item Which of the following are true? Select all that apply.
            \\begin{enumerate}[label=\\Alph*.]
                \\item Option A
                \\item Option B
            \\end{enumerate}
            \\begin{solution}
            A, B
            \\end{solution}

            \\end{enumerate}
            """;

        List<Question> questions = parser.parse(latex);

        assertEquals(QuestionType.SELECT_ALL, questions.get(0).questionType());
    }

    @Test
    void detectsSelectOne() {
        String latex = """
            \\begin{enumerate}

            \\qtag{Objective: Test}
            \\item What is the output? Select one answer.
            \\begin{enumerate}[label=\\Alph*.]
                \\item 42
                \\item 0
                \\item unpredictable
            \\end{enumerate}
            \\begin{solution}
            A
            \\end{solution}

            \\end{enumerate}
            """;

        List<Question> questions = parser.parse(latex);

        assertEquals(QuestionType.SELECT_ONE, questions.get(0).questionType());
    }

    @Test
void extractsMultilineSolutions() {
    String latex = """
        \\qtag{Objective: Test}
        \\item Question

        \\begin{solution}
        A, D

        A is correct because...
        D is correct because...
        \\end{solution}
        """;

    List<Question> questions = parser.parse(latex);

    assertThat(questions.get(0).solution())
            .contains("A, D");
}
}