package edu.kit.ifv.mobitopp.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class PatternActivityTest {

	private static final ActivityType type = ActivityType.BUSINESS;
	private static final int observedDuration = 0;
	private static final int duration = 1;

	@Test
	public void createsActivityAtFirstDay() {
		DayOfWeek weekDay = DayOfWeek.MONDAY;
		int minuteOfTheDay = 0;
		Time startTime = Time.start;
		PatternActivity oldConstructor = oldActivity(weekDay, minuteOfTheDay);
		PatternActivity newConstructor = newActivity(startTime);

		assertThat(oldConstructor, is(equalTo(newConstructor)));
		assertThat(oldConstructor.getWeekDayType(), is(equalTo(weekDay)));
		assertThat(oldConstructor.getStarttime(), is(minuteOfTheDay));
	}

	@Test
	public void createsActivityAtLaterDay() {
		DayOfWeek weekDay = DayOfWeek.TUESDAY;
		int minuteOfTheDay = 0;
		Time startTime = Time.start.nextDay();
		PatternActivity oldConstructor = oldActivity(weekDay, minuteOfTheDay);
		PatternActivity newConstructor = newActivity(startTime);

		assertThat(oldConstructor, is(equalTo(newConstructor)));
		assertThat(oldConstructor.getWeekDayType(), is(equalTo(weekDay)));
		assertThat(oldConstructor.getStarttime(), is(minuteOfTheDay));
	}
	
	@Test
	public void ensureWholeWeekAtHomeHasPositiveDuration() {
		assertThat(PatternActivity.maximumDuration, is(greaterThan(0)));
		assertThat(PatternActivity.WHOLE_WEEK_AT_HOME.getDuration(), is(greaterThan(0)));
	}

	@Test
  void decreaseStartDay() throws Exception {
	  int dayOffset = 1;
    Time firstDay = Time.start;
    Time secondDay = firstDay.plusDays(dayOffset);
    PatternActivity onSecondDay = newActivity(secondDay);
    
    PatternActivity decreaseStartDay = onSecondDay.decreaseStartDay(dayOffset);
    
    assertThat(decreaseStartDay.startTime(), is(equalTo(firstDay)));
  }
	
	private PatternActivity oldActivity(DayOfWeek weekDay, int startTime) {
		return new PatternActivity(type, weekDay, observedDuration, startTime, duration);
	}

	private PatternActivity newActivity(Time startTime) {
		return new PatternActivity(type, observedDuration, startTime, duration);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(PatternActivity.class).usingGetClass().verify();
	}
}
