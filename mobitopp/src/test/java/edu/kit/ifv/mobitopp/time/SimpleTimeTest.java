package edu.kit.ifv.mobitopp.time;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class SimpleTimeTest {

	private static final int daysPerWeek = 7;
	private Time date;
	private Time time;

	private final int week = 1;
	private final int day = week * daysPerWeek + 1;
	private final int hour = 6;
	private final int minute = 30;
	private final int second = 5;
	private Time monday;
	private Time tuesday;
	private Time wednesday;
	private Time thursday;
	private Time friday;
	private Time saturday;
	private Time sunday;
	private Time nextMonday;
	private Time same;
	private Time later;

	@BeforeEach
	public void setUp() {
		date = SimpleTime.ofDays(day);
		time = date.plusHours(hour).plusMinutes(minute).plusSeconds(second);
		same = SimpleTime.from(time.fromStart());
		later = time.plusSeconds(1);
		monday = new SimpleTime();
		tuesday = monday.nextDay();
		wednesday = tuesday.nextDay();
		thursday = wednesday.nextDay();
		friday = thursday.nextDay();
		saturday = friday.nextDay();
		sunday = saturday.nextDay();
		nextMonday = sunday.nextDay();
	}
	
	@Test
	public void startsAtMonday() {
		Time date = new SimpleTime();
		
		assertThat(date.weekDay(), is(DayOfWeek.MONDAY));
	}
	
	@Test
	public void oneWeek() {
		assertThat(SimpleTime.oneWeek(),
				contains(monday, tuesday, wednesday, thursday, friday, saturday, sunday));
	}

	@Test
	public void constructor() {
		assertUnchangedDayOf(date);
		assertZeroHourOf(date);
		assertZeroMinuteOf(date);
		assertZeroSecondOf(date);

		assertUnchangedDayOf(time);
		assertUnchangedHourOf(time);
		assertUnchangedMinuteOf(time);
		assertUnchangedSecondOf(time);
	}

	private void assertZeroSecondOf(Time date) {
		assertEquals("failure - second wrong", 0, date.getSecond());
	}

	private void assertZeroMinuteOf(Time date) {
		assertEquals("failure - minute wrong", 0, date.getMinute());
	}

	private void assertZeroHourOf(Time date) {
		assertEquals("failure - hour wrong", 0, date.getHour());
	}
	
	private void assertUnchangedSecondOf(Time date) {
		assertEquals("failure - second wrong", second, date.getSecond());
	}

	private void assertUnchangedMinuteOf(Time date) {
		assertEquals("failure - minute wrong", minute, date.getMinute());
	}

	private void assertUnchangedHourOf(Time date) {
		assertEquals("failure - hour wrong", hour, date.getHour());
	}

	private void assertUnchangedDayOf(Time date) {
		assertEquals("failure - day wrong", day, date.getDay());
	}

	private void assertChangedWeekOf(Time nextDate, int expected) {
		assertEquals("failure - week wrong", expected, nextDate.getWeek());
	}
	
	private void assertChangedDayOf(Time nextDate, int expected) {
		assertEquals("failure - day wrong", expected, nextDate.getDay());
	}

	private void assertChangedMinuteOf(Time next, int minute) {
		assertEquals("failure - minute wrong", minute, next.getMinute());
	}

	private void assertChangedHourOf(Time next, int hour) {
		assertEquals("failure - hour wrong", hour, next.getHour());
	}

	private void assertChangedSecondOf(Time next, int second) {
		assertEquals("failure - second wrong", second, next.getSecond());
	}

	@Test
	public void weekDay() {
		assertThat(monday.weekDay(), is(equalTo(DayOfWeek.MONDAY)));
		assertThat(tuesday.weekDay(), is(equalTo(DayOfWeek.TUESDAY)));
		assertThat(wednesday.weekDay(), is(equalTo(DayOfWeek.WEDNESDAY)));
		assertThat(thursday.weekDay(), is(equalTo(DayOfWeek.THURSDAY)));
		assertThat(friday.weekDay(), is(equalTo(DayOfWeek.FRIDAY)));
		assertThat(saturday.weekDay(), is(equalTo(DayOfWeek.SATURDAY)));
		assertThat(sunday.weekDay(), is(equalTo(DayOfWeek.SUNDAY)));
		assertThat(nextMonday.weekDay(), is(equalTo(DayOfWeek.MONDAY)));
	}
	
	@Test
	public void previousDay() {
		Time nextDate = date.previousDay();
		
		assertChangedDayOf(nextDate, day - 1);
		assertZeroHourOf(nextDate);
		assertZeroMinuteOf(nextDate);
		assertZeroSecondOf(nextDate);
		
		Time nextTime = time.previousDay();
		assertChangedDayOf(nextTime, day - 1);
		assertZeroHourOf(nextTime);
		assertZeroMinuteOf(nextTime);
		assertZeroSecondOf(nextTime);
	}
	
	@Test
	public void nextDay() {
		Time nextDate = date.nextDay();

		assertChangedDayOf(nextDate, day + 1);
		assertZeroHourOf(nextDate);
		assertZeroMinuteOf(nextDate);
		assertZeroSecondOf(nextDate);

		Time nextTime = time.nextDay();
		assertChangedDayOf(nextTime, day + 1);
		assertZeroHourOf(nextTime);
		assertZeroMinuteOf(nextTime);
		assertZeroSecondOf(nextTime);
	}
	
	@Test
	public void before() {
		assertTrue(time.isBefore(later));
	}
	
	@Test
	public void beforeOrEqual() {
		assertTrue(time.isBeforeOrEqualTo(later));
		assertTrue(time.isBeforeOrEqualTo(same));
	}
	
	@Test
	public void after() {
		assertTrue(later.isAfter(time));
	}

	@Test
	public void afterOrEqual() {
		assertTrue(later.isAfterOrEqualTo(time));
		assertTrue(time.isAfterOrEqualTo(same));
	}
	
	@Test
	public void compare() {
		assertThat(later.compareTo(time), is(greaterThan(0)));
		assertThat(time.compareTo(later), is(lessThan(0)));
		assertThat(same.compareTo(time), is(equalTo(0)));
	}

	@Test
	public void isMidnight() {
		assertTrue(date.isMidnight());
	}
	
	@Test
	public void isNotMidnight() {
		Time oneSecondAfter = date.plusSeconds(1);
		Time oneMinuteAfter = date.plusMinutes(1);
		Time oneHourAfter = date.plusHours(1);
		assertFalse(oneSecondAfter.isMidnight());
		assertFalse(oneMinuteAfter.isMidnight());
		assertFalse(oneHourAfter.isMidnight());
	}

	@Test
	public void getsNegativeDaysByMinute() {
		Time base = SimpleTime.ofDays(0);
		Time lastDay = base.minusSeconds(1);
		
		assertThat(base.getDay(), is(0));
		assertThat(lastDay.getDay(), is(-1));
	}
	
	@Test
	public void getsNegativeDaysByDay() {
		Time base = SimpleTime.ofDays(0);
		Time lastDay = base.previousDay();
		
		assertThat(base.getDay(), is(0));
		assertThat(lastDay.getDay(), is(-1));
	}

	@Test
	public void decrease() {
		int seconds = 1;
		Time changed = time.minus(RelativeTime.ofSeconds(seconds));
		
		assertChangedSecondOf(changed, second - seconds);
		assertUnchangedDayOf(changed);
		assertUnchangedHourOf(changed);
		assertUnchangedMinuteOf(changed);
	}
	
	@Test
	public void decreaseWeek() {
		int weeks = 1;
		Time changed = time.minusWeeks(weeks);
		
		assertChangedWeekOf(changed, week - weeks);
		assertChangedDayOf(changed, day - week * daysPerWeek);
		assertUnchangedHourOf(changed);
		assertUnchangedMinuteOf(changed);
		assertUnchangedSecondOf(changed);
	}
	
	@Test
	public void decreaseDay() {
		int days = 1;
		Time changed = time.minusDays(days);
		
		assertChangedDayOf(changed, day - days);
		assertUnchangedHourOf(changed);
		assertUnchangedMinuteOf(changed);
		assertUnchangedSecondOf(changed);
	}
	
	@Test
	public void decreaseHour() {
		int hours = 1;
		Time changed = time.minusHours(hours);
		
		assertChangedHourOf(changed, hour - hours);
		assertUnchangedDayOf(changed);
		assertUnchangedMinuteOf(changed);
		assertUnchangedSecondOf(changed);
	}
	
	@Test
	public void decreaseMinute() {
		int minutes = 1;
		Time changed = time.minusMinutes(minutes);
		
		assertChangedMinuteOf(changed, minute - minutes);
		assertUnchangedDayOf(changed);
		assertUnchangedHourOf(changed);
		assertUnchangedSecondOf(changed);
	}
	
	@Test
	public void decreaseSeconds() {
		int seconds = 1;
		Time changed = time.minusSeconds(seconds);
		
		assertChangedSecondOf(changed, second - seconds);
		assertUnchangedDayOf(changed);
		assertUnchangedHourOf(changed);
		assertUnchangedMinuteOf(changed);
	}
	
	@Test
	public void increase() {
		int seconds = 1;
		Time changed = time.plus(RelativeTime.ofSeconds(seconds));
		
		assertChangedSecondOf(changed, second + seconds);
		assertUnchangedDayOf(changed);
		assertUnchangedHourOf(changed);
		assertUnchangedMinuteOf(changed);
	}

	@Test
	public void increaseWeek() {
		int weeks = 1;
		Time changed = time.plusWeeks(weeks);
		
		assertChangedWeekOf(changed, week + weeks);
		assertChangedDayOf(changed, day + week * daysPerWeek);
		assertUnchangedHourOf(changed);
		assertUnchangedMinuteOf(changed);
		assertUnchangedSecondOf(changed);
	}
	
	@Test
	public void increaseDay() {
		int increment = 1;
		Time next = time.plusDays(increment);
		
		assertChangedDayOf(next, day + increment);
		assertUnchangedHourOf(next);
		assertUnchangedMinuteOf(next);
		assertUnchangedSecondOf(next);
	}
	@Test
	public void increaseMinute() {
		int inc_dd = 2;
		int inc_hh = 2;
		int inc_mm = 10;

		Time next = time.plusMinutes(inc_dd * 24 * 60 + inc_hh * 60 + inc_mm);

		assertChangedDayOf(next, day + inc_dd);
		assertChangedHourOf(next, hour + inc_hh);
		assertChangedMinuteOf(next, minute + inc_mm);
		assertUnchangedSecondOf(next);
	}

	@Test
	public void increaseSecond() {
		int inc_dd = 2;
		int inc_hh = 2;
		int inc_mm = 10;
		int inc_ss = 17;

		Time next = time
				.plusSeconds((((inc_dd * 24) + inc_hh) * 60 + inc_mm) * 60 + inc_ss);

		assertChangedDayOf(next, day + inc_dd);
		assertChangedHourOf(next, hour + inc_hh);
		assertChangedMinuteOf(next, minute + inc_mm);
		assertChangedSecondOf(next, second + inc_ss);
	}

	@Test
	public void startOfDay() {
		Time value = time.startOfDay();

		assertUnchangedDayOf(value);
		assertZeroHourOf(value);
		assertZeroMinuteOf(value);
		assertZeroSecondOf(value);
	}

	@Test
	public void newTime() {
		Time value = time.newTime(12, 15, 30);

		assertUnchangedDayOf(value);
		assertEquals("failure - hour wrong", 12, value.getHour());
		assertEquals("failure - minute wrong", 15, value.getMinute());
		assertEquals("failure - second wrong", 30, value.getSecond());
	}

	@Test
	public void newTimeInvalidSeconds() {
		assertThrows(IllegalArgumentException.class, () -> time.newTime(0, 0, 60));
	}

	@Test
	public void newTimeInvalidMinutes() {
	  assertThrows(IllegalArgumentException.class, () -> time.newTime(0, 60, 0));
	}

	@Test
	public void newTimeInvalidHours() {
	  assertThrows(IllegalArgumentException.class, () -> time.newTime(28, 0, 0));
	}

	@Test
	public void differenceInSeconds() {
		Time value = time.plusSeconds(61);

		assertEquals("failure - wrong difference", 61, value.differenceTo(time).seconds());
		assertEquals("failure - wrong difference", -61, time.differenceTo(value).seconds());
	}

	@Test
	public void differenceInMinutes() {
		Time value = time.plusSeconds(61);

		assertEquals("failure - wrong difference", 1, value.differenceTo(time).toMinutes());
		assertEquals("failure - wrong difference", -1, time.differenceTo(value).toMinutes());

		value = time.plusMinutes(3);

		assertEquals("failure - wrong difference", 3, value.differenceTo(time).toMinutes());
		assertEquals("failure - wrong difference", -3, time.differenceTo(value).toMinutes());
	}

	@Test
	public void testEquals() {
		Time value = time.startOfDay();

		assertEquals("failure - equals", date, date);
		assertEquals("failure - equals", time, time);

		assertEquals("failure - equals", date, value);
		assertNotEquals("failure - equals", time, value);
		assertFalse("failure - equals", time.equals(null));
	}
	
	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(SimpleTime.class).withIgnoredFields("weekDay").usingGetClass().verify();
	}
	
	@Test
	public void testHashCode() {
		Time value = time.startOfDay();

		assertEquals("failure - equals", date, date);
		assertEquals("failure - equals", time, time);
		assertEquals("failure - equals", date, value);

		assertEquals("failure - hashCode", date.hashCode(), date.hashCode());
		assertEquals("failure - hashCode", time.hashCode(), time.hashCode());
		assertEquals("failure - hashCode", date.hashCode(), value.hashCode());
	}

}
