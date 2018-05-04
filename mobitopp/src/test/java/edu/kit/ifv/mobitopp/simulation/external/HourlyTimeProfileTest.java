package edu.kit.ifv.mobitopp.simulation.external;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

public class HourlyTimeProfileTest {

	private static final int hoursPerDay = 24;

	@Test
	public void valuePerHour() {
		List<Float> onePerHourOfDay = elementsFor(hoursPerDay);
		HourlyTimeProfile profile = new HourlyTimeProfile(onePerHourOfDay);

		for (int hour = 0; hour < hoursPerDay; hour++) {
			double actual = profile.valueForHour(hour);
			assertThat(actual, is(closeTo(hour, 1e-6)));
		}
	}

	private List<Float> elementsFor(int hours) {
		return IntStream.range(0, hours).mapToObj(Float::valueOf).collect(toList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void tooManyHours() {
		int tooManyHours = hoursPerDay + 1;
		List<Float> onePerHourOfDay = elementsFor(tooManyHours);
		new HourlyTimeProfile(onePerHourOfDay);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tooFewHours() {
		int tooFewHours = hoursPerDay - 1;
		List<Float> onePerHourOfDay = elementsFor(tooFewHours);
		new HourlyTimeProfile(onePerHourOfDay);
	}

	@Test(expected=IllegalArgumentException.class)
	public void valueForTooLateHour() {
		List<Float> onePerHourOfDay = elementsFor(hoursPerDay);
		HourlyTimeProfile profile = new HourlyTimeProfile(onePerHourOfDay);
		
		profile.valueForHour(hoursPerDay);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void valueForNegativeHour() {
		List<Float> onePerHourOfDay = elementsFor(hoursPerDay);
		HourlyTimeProfile profile = new HourlyTimeProfile(onePerHourOfDay);
		
		profile.valueForHour(-1);
	}
}
