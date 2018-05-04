package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static edu.kit.ifv.mobitopp.simulation.publictransport.profilescan.Split.hoursOfSimulation;
import static edu.kit.ifv.mobitopp.time.RelativeTime.of;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.time.Time;

public class SplitTest {

	private Time timetableStart;
	
	@Before
	public void initialise() throws Exception {
		timetableStart = someTime();
	}

	@Test
	public void splitHourly() throws Exception {
		EntrySplitter hourly = Split.hourly(timetableStart);

		assertThat(hourly.parts(), is(equalTo(oneForEachHour())));
	}

	private Collection<EntryAcceptor> oneForEachHour() {
		List<EntryAcceptor> hours = new ArrayList<>();
		for (int hour = 0; hour < hoursOfSimulation; hour++) {
			Time current = timetableStart.plus(of(hour, HOURS));
			hours.add(validityOf(current));
		}
		return hours;
	}
	
	@Test
	public void createsValidity() {
		int hour = 0;
		EntrySplitter splitter = Split.hourly(timetableStart);
		
		Validity validity = splitter.validity(hour);
		
		Validity absolute = validityOf(timetableStart.plus(of(hour, HOURS)));
		assertThat(validity, is(equalTo(absolute)));
	}
	
	@Test
	public void createsValidityFromAbsoluteTime() {
		Time time = someTime();
		EntrySplitter splitter = Split.hourly(timetableStart);
		
		Validity validity = splitter.validity(time);
		
		assertThat(validity, is(equalTo(validityOf(time))));
	}

	private Accept validityOf(Time time) {
		return Accept.perHour(time);
	}
}
