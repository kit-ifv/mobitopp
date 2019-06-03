package edu.kit.ifv.mobitopp.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class RelativeTime implements Comparable<RelativeTime> {

  static final int secondsPerMinute = 60;
  static final int minutesPerHour = 60;
  static final int hoursPerDay = 24;
  static final int daysPerWeek = 7;

  static final int secondsPerWeek = daysPerWeek * hoursPerDay * minutesPerHour * secondsPerMinute;
  static final int secondsPerDay = hoursPerDay * minutesPerHour * secondsPerMinute;
  static final int secondsPerHour = minutesPerHour * secondsPerMinute;

  public static final RelativeTime ZERO = RelativeTime.ofSeconds(0);
  public static final RelativeTime INFINITE = RelativeTime.ofDays(100);
  private final int seconds;

  private RelativeTime(int seconds) {
    super();
    this.seconds = seconds;
  }

  private RelativeTime(Duration duration) {
    this(Math.toIntExact(duration.getSeconds()));
  }

  public int seconds() {
    return seconds;
  }

  public int toMinutes() {
    return seconds / secondsPerMinute;
  }

  public int toHours() {
    return toMinutes() / minutesPerHour;
  }

  public int toDays() {
    return toHours() / hoursPerDay;
  }

  public int toWeeks() {
    return toDays() / daysPerWeek;
  }

  public RelativeTime plus(RelativeTime other) {
    return new RelativeTime(Math.addExact(this.seconds, other.seconds));
  }

  public RelativeTime plusWeeks(int weeks) {
    return plusSeconds(weeks * secondsPerWeek);
  }

  public RelativeTime plusDays(int days) {
    return plusSeconds(days * secondsPerDay);
  }

  public RelativeTime plusHours(int hours) {
    return plusSeconds(hours * secondsPerHour);
  }

  public RelativeTime plusMinutes(int minutes) {
    return plusSeconds(minutes * secondsPerMinute);
  }

  public RelativeTime plusSeconds(int seconds) {
    return new RelativeTime(Math.addExact(this.seconds, seconds));
  }

  public RelativeTime minus(RelativeTime other) {
    return new RelativeTime(Math.subtractExact(this.seconds, other.seconds));
  }

  public RelativeTime minusWeeks(int weeks) {
    return minusSeconds(weeks * secondsPerWeek);
  }

  public RelativeTime minusDays(int days) {
    return minusSeconds(days * secondsPerDay);
  }

  public RelativeTime minusHours(int hours) {
    return minusSeconds(hours * secondsPerHour);
  }

  public RelativeTime minusMinutes(int minutes) {
    return minusSeconds(minutes * secondsPerMinute);
  }

  public RelativeTime minusSeconds(int seconds) {
    return new RelativeTime(Math.subtractExact(this.seconds, seconds));
  }

  public RelativeTime multiplyBy(double multiplicant) {
    int product = Math.toIntExact(Math.round(seconds * multiplicant));
    return RelativeTime.ofSeconds(product);
  }

  public Duration toDuration() {
    return Duration.ofSeconds(seconds);
  }

  public boolean isNegative() {
    return 0 > seconds;
  }

  @Override
  public int compareTo(RelativeTime other) {
    return Long.compare(this.seconds, other.seconds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(seconds);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RelativeTime other = (RelativeTime) obj;
    return seconds == other.seconds;
  }

  @Override
  public String toString() {
    return "RelativeTime [seconds=" + seconds + "]";
  }

  public static RelativeTime ofWeeks(int weeks) {
    return new RelativeTime(weeks * secondsPerWeek);
  }

  public static RelativeTime ofDays(int days) {
    return new RelativeTime(days * secondsPerDay);
  }

  public static RelativeTime ofHours(int hour) {
    return new RelativeTime(hour * secondsPerHour);
  }

  public static RelativeTime ofMinutes(int minutes) {
    return new RelativeTime(minutes * secondsPerMinute);
  }

  public static RelativeTime ofSeconds(int seconds) {
    return new RelativeTime(seconds);
  }

  public static RelativeTime of(Duration duration) {
    return new RelativeTime(duration);
  }

  public static RelativeTime of(int amount, ChronoUnit unit) {
    return new RelativeTime(Duration.of(amount, unit));
  }

}
