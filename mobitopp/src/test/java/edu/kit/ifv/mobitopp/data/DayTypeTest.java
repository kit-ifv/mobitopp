package edu.kit.ifv.mobitopp.data;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.time.Time;

public class DayTypeTest {

	private static final Time monday = Data.someTime();
	private static final Time tuesday = monday.nextDay();
	private static final Time wednesday = tuesday.nextDay();
	private static final Time thursday = wednesday.nextDay();
	private static final Time friday = thursday.nextDay();
	private static final Time saturday = friday.nextDay();
	private static final Time sunday = saturday.nextDay();

	@Test
	public void isWeekday() {
		for (Time weekday : asList(monday, tuesday, wednesday, thursday, friday)) {
			DayType dayType = DayType.from(weekday);

			assertThat(dayType, is(equalTo(DayType.weekdays)));
		}
	}

	@Test
	public void isSaturday() {
		DayType dayType = DayType.from(saturday);

		assertThat(dayType, is(equalTo(DayType.saturday)));
	}

	@Test
	public void isSunday() {
		DayType dayType = DayType.from(sunday);

		assertThat(dayType, is(equalTo(DayType.sunday)));
	}
}
