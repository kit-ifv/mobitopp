package edu.kit.ifv.mobitopp.visum.routes;

import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public final class VisumUtils {

  private static final long MINUTES_PER_HOUR = 60;
  private static final long SECONDS_PER_MINUTE = 60;
  private static final long SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
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
        long hoursAsSecs = parseNumber(text, hourMatch, SECONDS_PER_HOUR, "hours");
        long minsAsSecs = parseNumber(text, minuteMatch, SECONDS_PER_MINUTE, "minutes");
        long seconds = parseNumber(text, secondMatch, 1, "seconds");
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

  private static long parseNumber(String text, String parsed, long multiplier, String errorText) {
 // regex limits to [0-9]+
    if (parsed == null) {
        return 0;
    }
    try {
        long val = Long.parseLong(parsed);
        return Math.multiplyExact(val, multiplier);
    } catch (NumberFormatException | ArithmeticException ex) {
        throw (DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: " + errorText, text, 0).initCause(ex);
    }
  }

  private static RelativeTime create(long hoursAsSecs, long minsAsSecs, long seconds) {
    int duration = Math.toIntExact(Math.addExact(hoursAsSecs, Math.addExact(minsAsSecs, seconds)));
    return RelativeTime.ofSeconds(duration);
  }
}
