# Objective2270

A Java tool for parsing LaTeX exam files from CS 2270 and extracting questions organized by learning objective.

## What it does

Reads LaTeX source files that use the `\qtag{Objective: ...}` convention and extracts each question along with:
- The learning objective it targets
- The question number
- The correct solution
- The raw LaTeX for the question
- The question type (true/false, select one, or select all that apply)
- Extracts information about the exam (Fall vs Spring, Year, version letter ) from the .tex filename that in read from the examdrop folder.

## LaTeX format expected

Questions must be inside an `\begin{enumerate}` block, tagged with `\qtag`, and have solutions wrapped in a `\begin{solution}` environment:

```latex
\begin{enumerate}

\qtag{Objective: Can identify properties of heap and stack memory.}
\item Which of the following are True about stack memory?

\begin{enumerate}[label=\Alph*.]
    \item Local variables
    \item Long lived variables
\end{enumerate}

\begin{solution}
A
\end{solution}

\end{enumerate}
```
## Question type detection

Each question is classified as one of three types:

| Type | Detection rule |
|------|---------------|
| `TRUE_FALSE` | Answer choices are exactly `\item True` and `\item False` |
| `SELECT_ALL` | Question text contains "select all that apply" |
| `SELECT_ONE` | Question text contains "select one" or "choose one", or no other signal matches |

The true/false check takes priority. A question with True/False answer choices is always classified as `TRUE_FALSE` regardless of what the question text says.

## Assumptions for version 1

One .tex file per exam version.
Every question starts with \qtag{.
Every question contains exactly one outer \item.
Questions end with \end{solution}.
No question subparts.
Inner multiple-choice options may contain additional \items.
Solutions always exist.
One objective per question.

## Building and running

Requires Java and Gradle.

```bash
./gradlew build
./gradlew test
```

## Project structure

```
app/src/main/java/objective2270/
  App.java         - Entry point
  Parser.java      - Parser interface
  Spr26Parser.java - Parser implementation for Spring 2026 exam format
  Question.java    - Data record: questionNumber, objectiveText, solution, rawLatex, questionType

app/src/test/java/objective2270/
  ParseTest.java   - Unit tests for the parser
```
