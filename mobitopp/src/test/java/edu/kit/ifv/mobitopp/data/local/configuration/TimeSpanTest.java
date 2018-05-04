package edu.kit.ifv.mobitopp.data.local.configuration;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.time.Time;

public class TimeSpanTest {

	private static final int start = 1;
	private static final int end = 2;

	private TimeSpan timeSpan;
	private TimeSpan firstHour;
	private TimeSpan secondHour;
	private Time firstDay;
	private Time startOfSpan;
	private Time endOfSpan;
	private Time afterSpan;

	@Before
	public void initialise() {
		timeSpan = TimeSpan.between(start, end);
		firstHour = new TimeSpan(start);
		secondHour = new TimeSpan(end);
		firstDay = Data.someTime();
		startOfSpan = firstDay.plusHours(1);
		endOfSpan = startOfSpan.plusHours(1);
		afterSpan = endOfSpan.plusHours(1);
	}

	@Test
	public void matches() {
		assertTrue(timeSpan.matches(startOfSpan));
	}

	@Test
	public void matchesInclusiveEnd() {
		assertTrue(timeSpan.matches(endOfSpan));
	}

	@Test
	public void dateAfter() {
		assertFalse(timeSpan.matches(afterSpan));
	}

	@Test
	public void dateBefore() {
		assertFalse(timeSpan.matches(firstDay));
	}

	@Test
	public void allHoursInclusive() {
		assertThat(timeSpan.hours().collect(toList()), is(timeSpanPerHour()));
	}

	private List<TimeSpan> timeSpanPerHour() {
		return asList(firstHour, secondHour);
	}

	@Test
	public void singleHourTimeSpan() {
		assertThat(firstHour.getFrom(), is(start));
		assertThat(firstHour.getTo(), is(start));
	}

	@Test
	public void containsItself() {
		assertTrue(timeSpan.contains(timeSpan));
	}

	@Test
	public void containsSingleHours() {
		assertTrue(timeSpan.contains(firstHour));
		assertTrue(timeSpan.contains(secondHour));
	}

	@Test
	public void containsOutOfSpanHours() {
		TimeSpan tooEarly = new TimeSpan(start - 1);
		TimeSpan tooLate = new TimeSpan(end + 1);
		assertFalse(timeSpan.contains(tooEarly));
		assertFalse(timeSpan.contains(tooLate));
	}

	@Test
	public void add() {
		TimeSpan sum = firstHour.add(secondHour);

		assertThat(sum, is(not(sameInstance(firstHour))));
		assertThat(sum, is(not(sameInstance(secondHour))));
		assertThat(sum, is(equalTo(TimeSpan.between(start, end))));
	}

	@Test(expected=IllegalArgumentException.class)
	public void addNonNeighbourSpans() {
		TimeSpan thirdHour = new TimeSpan(3);
		
		firstHour.add(thirdHour);
	}
}
