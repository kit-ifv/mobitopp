package edu.kit.ifv.mobitopp.visum.routes;

import java.time.Duration;
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

  public static Duration parseTime(String time) {
    Objects.requireNonNull(time, "time");
    if (time.isEmpty()) {
      return RelativeTime.ZERO.toDuration();
    }
    String text = time.replaceAll(" ", "");
    
    Matcher matcher = PATTERN.matcher(text);
    if (matcher.matches()) {
      String hourMatch = matcher.group(1);
      String minuteMatch = matcher.group(2);
      String secondMatch = matcher.group(3);
      String nanosecondMatch = matcher.group(4);
      if (hourMatch != null || minuteMatch != null || secondMatch != null) {
        int hoursAsSecs = parseNumber(text, hourMatch, RelativeTime.secondsPerHour, "hours");
        int minsAsSecs = parseNumber(text, minuteMatch, RelativeTime.secondsPerMinute, "minutes");
        int seconds = parseNumber(text, secondMatch, 1, "seconds");
        int nanoSeconds = parseFraction(text, nanosecondMatch);
        try {
          return create(hoursAsSecs, minsAsSecs, seconds, nanoSeconds);
        } catch (ArithmeticException ex) {
          throw (DateTimeParseException) new DateTimeParseException(
              "Text cannot be parsed to a Duration: overflow", text, 0).initCause(ex);
        }
      }
    }
    throw new DateTimeParseException("Text cannot be parsed to a Duration", text, 0);
  }

  /**
   * Parses fraction of seconds in the time string. Empty matcher groups are allowed to be <code>null</code>.
   */
  private static int parseFraction(String text, String parsed) {
    if (null == parsed) {
      return 0;
    }
    try {
      int nanoSecondsLength = 9;
      int lastChar = Math.min(nanoSecondsLength, parsed.length());
      int fraction = Integer.parseInt(parsed.substring(0, lastChar));
      for(int i = 0; i < nanoSecondsLength - parsed.length(); i++) {
        fraction *= 10;
      }
      return fraction;
    } catch (NumberFormatException | ArithmeticException ex) {
      throw (DateTimeParseException) new DateTimeParseException(
          "Text cannot be parsed to a Duration: nano seconds", text, 0).initCause(ex);
    }
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

  private static Duration create(int hoursAsSecs, int minsAsSecs, int seconds, int nanoSeconds) {
    int duration = Math.toIntExact(Math.addExact(hoursAsSecs, Math.addExact(minsAsSecs, seconds)));
    return Duration.ofSeconds(duration, nanoSeconds);
  }
}
