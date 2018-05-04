package edu.kit.ifv.mobitopp.time;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DayOfWeekTest {

	@Test
	public void resolveDayToDayOfWeek() {
		DayOfWeek resolved = DayOfWeek.fromDay(0);

		assertThat(resolved, is(equalTo(DayOfWeek.MONDAY)));
	}
	
	@Test
	public void resolveDayAfterWeek() {
		DayOfWeek resolved = DayOfWeek.fromDay(7);

		assertThat(resolved, is(equalTo(DayOfWeek.MONDAY)));
	}
	
	@Test
	public void resolveDayBeforeWeek() {
		DayOfWeek resolved = DayOfWeek.fromDay(-1);

		assertThat(resolved, is(equalTo(DayOfWeek.SUNDAY)));
	}
	
	@Test
	public void next() {
		assertThat(DayOfWeek.MONDAY.next(), is(equalTo(DayOfWeek.TUESDAY)));
		assertThat(DayOfWeek.TUESDAY.next(), is(equalTo(DayOfWeek.WEDNESDAY)));
		assertThat(DayOfWeek.WEDNESDAY.next(), is(equalTo(DayOfWeek.THURSDAY)));
		assertThat(DayOfWeek.THURSDAY.next(), is(equalTo(DayOfWeek.FRIDAY)));
		assertThat(DayOfWeek.FRIDAY.next(), is(equalTo(DayOfWeek.SATURDAY)));
		assertThat(DayOfWeek.SATURDAY.next(), is(equalTo(DayOfWeek.SUNDAY)));
		assertThat(DayOfWeek.SUNDAY.next(), is(equalTo(DayOfWeek.MONDAY)));
	}
}
