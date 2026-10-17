package com.timesquest.core;

import java.util.*;

/** Device-independent learning rules. No clock, network or Android dependencies. */
public final class LearningEngine {
  public static final int FACTS = 144;
  public static final long DAY = 86_400_000L;

  public record Question(int a, int b) {
    public Question {
      if (a < 1 || a > 12 || b < 1 || b > 12) throw new IllegalArgumentException("Tables are 1–12");
    }

    public int answer() {
      return a * b;
    }

    public int id() {
      return (a - 1) * 12 + b - 1;
    }

    public String expression() {
      return a + " × " + b;
    }

    public List<Integer> choices(Random random) {
      Set<Integer> candidates = new LinkedHashSet<>();
      for (int offset : new int[] {-2, -1, 1, 2}) {
        candidates.add(answer() + offset * a);
        candidates.add(answer() + offset * b);
      }
      for (int offset = -4; offset <= 4; offset++) candidates.add(answer() + offset);
      candidates.add(a + b); // A common addition-versus-multiplication misconception.
      if (answer() >= 10 && answer() < 100) {
        int reversed = (answer() % 10) * 10 + answer() / 10;
        if (Math.abs(reversed - answer()) <= Math.max(6, 2 * Math.max(a, b)))
          candidates.add(reversed);
      }
      candidates.removeIf(value -> value < 1 || value > 144 || value == answer());
      List<Integer> wrong = new ArrayList<>(candidates);
      Collections.shuffle(wrong, random);
      // Sample the values as well as their positions. Keep this set stable for retries/saves.
      List<Integer> result = new ArrayList<>(wrong.subList(0, 3));
      result.add(answer());
      Collections.shuffle(result, random);
      return result;
    }

    public String repeatedAddition() {
      return String.join(" + ", Collections.nCopies(a, String.valueOf(b))) + " = " + answer();
    }

    public String skipCount() {
      List<String> n = new ArrayList<>();
      for (int i = 1; i <= a; i++) n.add(String.valueOf(i * b));
      return String.join(", ", n);
    }

    public String explanation() {
      return a
          + " equal groups of "
          + b
          + " make "
          + answer()
          + ".\n"
          + repeatedAddition()
          + "\nSwap the groups: "
          + b
          + " × "
          + a
          + " = "
          + answer()
          + ".\nFact family: "
          + answer()
          + " ÷ "
          + b
          + " = "
          + a
          + ".";
    }

    public String hint(int given) {
      if (given == a + b && answer() != given)
        return "That adds the two numbers. Multiplication means equal groups. " + explanation();
      if (Math.abs(given - answer()) == a || Math.abs(given - answer()) == b)
        return "You may have counted one group too many or too few. Count by "
            + b
            + ": "
            + skipCount()
            + ".";
      return explanation();
    }
  }

  public static final class Fact {
    public int attempts, correct, streak, assisted;
    public long due, totalMillis;

    public boolean mastered() {
      return streak >= 3 && correct >= 3;
    }
  }

  public final Fact[] facts = new Fact[FACTS];
  private final Random random;

  public LearningEngine(Random random) {
    this.random = random;
    for (int i = 0; i < FACTS; i++) facts[i] = new Fact();
  }

  public Question next(int table, int maxTable, long now, int previousId) {
    if (table < 0 || table > 12 || maxTable < 1 || maxTable > 12)
      throw new IllegalArgumentException();
    List<Question> pool = new ArrayList<>();
    List<Double> weights = new ArrayList<>();
    double totalWeight = 0;
    for (int a = 1; a <= 12; a++)
      for (int b = 1; b <= 12; b++) {
        if (table > 0 ? a != table : a > maxTable) continue;
        Question q = new Question(a, b);
        Fact f = facts[q.id()];
        if (q.id() == previousId) continue;
        double errorWeight = f.attempts == 0 ? 0 : 6.0 * (f.attempts - f.correct) / f.attempts;
        double score =
            (f.attempts == 0 ? 4 : 0)
                + (f.due <= now ? 8 : 0)
                + errorWeight
                - Math.min(f.streak, 6) * 2;
        // Weight review needs instead of always picking only the highest-scoring facts.
        // This preserves extra practice for weak facts without a predictable two-fact loop.
        double weight = Math.pow(2, score / 4);
        pool.add(q);
        weights.add(weight);
        totalWeight += weight;
      }
    double pick = random.nextDouble() * totalWeight;
    for (int i = 0; i < pool.size(); i++) {
      pick -= weights.get(i);
      if (pick < 0) return pool.get(i);
    }
    return pool.get(pool.size() - 1);
  }

  /** Call exactly once per question, using the first answer; retries never create mastery. */
  public void record(Question q, int answer, long millis, boolean usedHint, long now) {
    Fact f = facts[q.id()];
    f.attempts++;
    f.totalMillis += Math.max(0, Math.min(millis, 600_000));
    boolean right = q.answer() == answer;
    if (right) f.correct++;
    if (usedHint) f.assisted++;
    f.streak = right && !usedHint ? Math.min(30, f.streak + 1) : 0;
    f.due = now + (f.streak == 0 ? 60_000 : DAY * (1L << Math.min(5, f.streak - 1)));
  }

  public int mastered(int table) {
    int n = 0;
    for (int i = (table - 1) * 12; i < table * 12; i++) if (facts[i].mastered()) n++;
    return n;
  }

  public int attempts(int table) {
    int n = 0;
    for (int i = (table - 1) * 12; i < table * 12; i++) n += facts[i].attempts;
    return n;
  }

  public int accuracy(int table) {
    int c = 0, a = 0;
    for (int i = (table - 1) * 12; i < table * 12; i++) {
      a += facts[i].attempts;
      c += facts[i].correct;
    }
    return a == 0 ? 0 : Math.round(c * 100f / a);
  }

  public record TableProgress(
      int attemptedFacts,
      int attempts,
      int correct,
      int assisted,
      int mastered,
      int dueReviews,
      long averageMillis) {}

  public TableProgress progress(int table, long now) {
    if (table < 1 || table > 12) throw new IllegalArgumentException("Tables are 1–12");
    int count = 0, attempts = 0, correct = 0, assisted = 0, mastered = 0, due = 0;
    long time = 0;
    for (int i = (table - 1) * 12; i < table * 12; i++) {
      Fact f = facts[i];
      if (f.attempts > 0) {
        count++;
        if (f.due <= now) due++;
      }
      attempts += f.attempts;
      correct += f.correct;
      assisted += f.assisted;
      if (f.mastered()) mastered++;
      time += f.totalMillis;
    }
    return new TableProgress(
        count, attempts, correct, assisted, mastered, due, attempts == 0 ? 0 : time / attempts);
  }

  public int suggestedTable(long now) {
    int chosen = 2;
    double best = -1;
    for (int table = 1; table <= 12; table++) {
      TableProgress p = progress(table, now);
      if (p.attempts() == 0) continue;
      double score =
          p.dueReviews() * 2
              + 6.0 * (p.attempts() - p.correct()) / p.attempts()
              + (12 - p.mastered()) * .1;
      if (score > best) {
        best = score;
        chosen = table;
      }
    }
    return chosen;
  }

  public String serialize() {
    StringBuilder s = new StringBuilder("TQ1\n");
    for (Fact f : facts)
      s.append(f.attempts)
          .append(',')
          .append(f.correct)
          .append(',')
          .append(f.streak)
          .append(',')
          .append(f.assisted)
          .append(',')
          .append(f.due)
          .append(',')
          .append(f.totalMillis)
          .append('\n');
    return s.toString();
  }

  /** Atomic recovery: reject damaged saves instead of partly loading them. */
  public boolean restore(String data) {
    try {
      String[] rows = data.split("\n");
      if (rows.length != 145 || !rows[0].equals("TQ1")) return false;
      Fact[] restored = new Fact[FACTS];
      for (int i = 0; i < FACTS; i++) {
        String[] v = rows[i + 1].split(",");
        if (v.length != 6) return false;
        Fact f = new Fact();
        f.attempts = Integer.parseInt(v[0]);
        f.correct = Integer.parseInt(v[1]);
        f.streak = Integer.parseInt(v[2]);
        f.assisted = Integer.parseInt(v[3]);
        f.due = Long.parseLong(v[4]);
        f.totalMillis = Long.parseLong(v[5]);
        if (f.attempts < 0
            || f.correct < 0
            || f.correct > f.attempts
            || f.streak < 0
            || f.streak > 30
            || f.streak > f.correct
            || f.assisted < 0
            || f.assisted > f.attempts
            || f.due < 0
            || f.totalMillis < 0) return false;
        restored[i] = f;
      }
      System.arraycopy(restored, 0, facts, 0, FACTS);
      return true;
    } catch (RuntimeException ignored) {
      return false;
    }
  }
}
