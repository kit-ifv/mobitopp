package edu.kit.ifv.mobitopp.time;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class RelativeTimeTest {

  @Test
  public void madeUpOfSeconds() throws Exception {
    RelativeTime time = RelativeTime.of(1, SECONDS);

    assertThat(time.seconds(), is(1));
  }

  @Test
  public void oneMinuteInSeconds() throws Exception {
    RelativeTime time = RelativeTime.of(1, ChronoUnit.MINUTES);

    assertThat(time.seconds(), is(RelativeTime.secondsPerMinute));
  }

  @Test
  public void oneHourInSeconds() throws Exception {
    RelativeTime time = RelativeTime.of(1, ChronoUnit.HOURS);

    assertThat(time.seconds(), is(RelativeTime.secondsPerHour));
  }

  @Test
  void getter() throws Exception {
    int weeks = 1;
    int days = 2;
    int hours = 3;
    int minutes = 4;
    int seconds = 5;
    int allDays = weeks * RelativeTime.daysPerWeek + days;
    int allHours = allDays * RelativeTime.hoursPerDay + hours;
    int allMinutes = allHours * RelativeTime.minutesPerHour + minutes;
    int allSeconds = allMinutes * RelativeTime.secondsPerMinute + seconds;

    RelativeTime time = RelativeTime
        .ofWeeks(weeks)
        .plusDays(days)
        .plusHours(hours)
        .plusMinutes(minutes)
        .plusSeconds(seconds);

    assertAll(() -> assertThat("weeks", time.toWeeks(), is(weeks)),
        () -> assertThat("days", time.toDays(), is(allDays)),
        () -> assertThat("hours", time.toHours(), is(allHours)),
        () -> assertThat("minutes", time.toMinutes(), is(allMinutes)),
        () -> assertThat("seconds", time.seconds(), is(allSeconds)));
  }

  @Test
  public void plus() {
    RelativeTime some = RelativeTime.ofSeconds(1);
    RelativeTime another = RelativeTime.ofSeconds(1);

    RelativeTime sum = some.plus(another);

    assertThat(sum, is(RelativeTime.ofSeconds(2)));
  }
  
  @Test
  public void plusSpecific() {
    int value = 1;
    int result = 2;
    assertAll(
        () -> assertThat(RelativeTime.ofWeeks(value).plusWeeks(value),
            is(RelativeTime.ofWeeks(result))),
        () -> assertThat(RelativeTime.ofDays(value).plusDays(value),
            is(RelativeTime.ofDays(result))),
        () -> assertThat(RelativeTime.ofHours(value).plusHours(value),
            is(RelativeTime.ofHours(result))),
        () -> assertThat(RelativeTime.ofMinutes(value).plusMinutes(value),
            is(RelativeTime.ofMinutes(result))),
        () -> assertThat(RelativeTime.ofSeconds(value).plusSeconds(value),
            is(RelativeTime.ofSeconds(result))));
  }

  @Test
  public void minus() {
    RelativeTime some = RelativeTime.ofSeconds(1);
    RelativeTime another = RelativeTime.ofSeconds(1);

    RelativeTime difference = some.minus(another);

    assertThat(difference, is(RelativeTime.ZERO));
  }

  @Test
  public void minusSpecific() {
    int value = 1;
    assertAll(
        () -> assertThat(RelativeTime.ofWeeks(value).minusWeeks(value), is(RelativeTime.ZERO)),
        () -> assertThat(RelativeTime.ofDays(value).minusDays(value), is(RelativeTime.ZERO)),
        () -> assertThat(RelativeTime.ofHours(value).minusHours(value), is(RelativeTime.ZERO)),
        () -> assertThat(RelativeTime.ofMinutes(value).minusMinutes(value), is(RelativeTime.ZERO)),
        () -> assertThat(RelativeTime.ofSeconds(value).minusSeconds(value), is(RelativeTime.ZERO)));
  }

  @Test
  public void multiply() {
    RelativeTime some = RelativeTime.ofSeconds(1);
    RelativeTime another = RelativeTime.ofSeconds(2);

    RelativeTime product = some.multiplyBy(2.0d);

    assertThat(product, is(equalTo(another)));
  }

  @Test
  public void isNegative() {
    RelativeTime time = RelativeTime.ofSeconds(-1);

    assertTrue(time.isNegative());
  }

  @Test
  public void isPositive() {
    RelativeTime time = RelativeTime.ofSeconds(1);

    assertFalse(time.isNegative());
  }

  @Test
  public void toFirstWeek() {
    RelativeTime week = RelativeTime.ofDays(6);

    int weeks = week.toWeeks();

    assertThat(weeks, is(0));
  }

  @Test
  public void toSecondWeek() {
    RelativeTime week = RelativeTime.ofDays(7);

    int weeks = week.toWeeks();

    assertThat(weeks, is(1));
  }

  @Test
  void compare() throws Exception {
    RelativeTime low = RelativeTime.ofSeconds(0);
    RelativeTime high = RelativeTime.ofSeconds(1);

    assertAll(() -> assertThat("lower", low.compareTo(high), is(lessThan(0))),
        () -> assertThat("higher", high.compareTo(low), is(greaterThan(0))),
        () -> assertThat("low equal", low.compareTo(low), is(equalTo(0))),
        () -> assertThat("high equal", high.compareTo(high), is(equalTo(0))));
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    EqualsVerifier.forClass(RelativeTime.class).usingGetClass().verify();
  }
}
