package edu.kit.ifv.mobitopp.visum.routes;

import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public final class VisumUtils {

  private static final Pattern PATTERN = Pattern
      .compile("(?:([0-9]+)H)?(?:([0-9]+)M(?:in)?)?(?:([0-9]+)(?:[.,]([0-9]{0,20}))?S)?",
          Pattern.CASE_INSENSITIVE);

  private VisumUtils() {
    super();
  }

  public static RelativeTime parseTime(String time) {
    Objects.requireNonNull(time, "time");
    if (time.isEmpty()) {
      return RelativeTime.ZERO;
    }
    String text = time.replaceAll(" ", "");
    Matcher matcher = PATTERN.matcher(text);
    if (matcher.matches()) {
      String hourMatch = matcher.group(1);
      String minuteMatch = matcher.group(2);
      String secondMatch = matcher.group(3);
      if (hourMatch != null || minuteMatch != null || secondMatch != null) {
        int hoursAsSecs = parseNumber(text, hourMatch, RelativeTime.secondsPerHour, "hours");
        int minsAsSecs = parseNumber(text, minuteMatch, RelativeTime.secondsPerMinute, "minutes");
        int seconds = parseNumber(text, secondMatch, 1, "seconds");
        try {
          return create(hoursAsSecs, minsAsSecs, seconds);
        } catch (ArithmeticException ex) {
          throw (DateTimeParseException) new DateTimeParseException(
              "Text cannot be parsed to a Duration: overflow", text, 0).initCause(ex);
        }
      }
    }
    throw new DateTimeParseException("Text cannot be parsed to a Duration", text, 0);
  }

  /**
   * Parses numbers in the time string. Empty matcher groups are allowed to be <code>null</code>.
   */
  private static int parseNumber(String text, String parsed, int multiplier, String errorText) {
    if (null == parsed) {
      return 0;
    }
    try {
      int val = Integer.parseInt(parsed);
      return Math.multiplyExact(val, multiplier);
    } catch (NumberFormatException | ArithmeticException ex) {
      throw (DateTimeParseException) new DateTimeParseException(
          "Text cannot be parsed to a Duration: " + errorText, text, 0).initCause(ex);
    }
  }

  private static RelativeTime create(int hoursAsSecs, int minsAsSecs, int seconds) {
    int duration = Math.toIntExact(Math.addExact(hoursAsSecs, Math.addExact(minsAsSecs, seconds)));
    return RelativeTime.ofSeconds(duration);
  }
}
