# Multiplication question variety

Changed answer generation so a repeated fact can offer different plausible wrong answers, including nearby products, small counting errors and addition confusion. Each question still has exactly four distinct positive choices and exactly one correct answer. Both the chosen values and button positions are shuffled. Choices remain stable while retrying or restoring the same question.

Practice now samples facts by review priority rather than choosing only the highest score. Weak facts receive extra practice, other eligible facts remain possible, and the immediately previous fact is excluded. Table/world restrictions and first-attempt mastery rules remain intact.

Validation: all 19 core tests passed, including three new regression tests. These cover every multiplication fact with 100 answer sets each, answer-position distribution and varying wrong-answer sets for a repeated fact, and escape from a two-weak-fact practice loop. Standalone debug and release Android unit tests passed; standalone debug and instrumentation APKs built successfully. Physical device instrumentation is pending while the Fire is locked.

These changes are currently in the source/debug build. Amazon LAT TEST 1 and the uploaded public draft remain 0.1.3/code 4 and do not yet contain them. The public app remains unsubmitted.
