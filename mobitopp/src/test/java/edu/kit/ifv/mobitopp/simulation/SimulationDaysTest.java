package edu.kit.ifv.mobitopp.simulation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.time.Time;

public class SimulationDaysTest {

	private static final Time someDate = SimulationDays.simulationStart();
	private static final int days = 2;
	
	private SimulationDays input;

	@Before
	public void initialise() {
		new ArrayList<>();
		input = newBaseInputSpecification();
	}

	@Test
	public void startDate() {
		Time startDate = input.startDate();

		assertThat(startDate, is(someDate));
	}
	
	@Test
	public void createSeveralDays() {
		int days = 3;
		SimulationDays specification = newSimulationDays(days);
		
		List<Time> simulationDates = specification.simulationDates();
		
		assertThat(simulationDates, hasSize(days));
		assertThat(simulationDates, hasItem(someDate));
		assertThat(simulationDates, hasItem(someDate.nextDay()));
		assertThat(simulationDates, hasItem(someDate.nextDay().nextDay()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativePeriod() {
		newSimulationDays(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noSimulationDay() {
		newSimulationDays(0);
	}
	
	@Test
	public void endDay() {
		Time endDate = newSimulationDays(1).endDate();
		
		assertThat(endDate, is(equalTo(someDate.nextDay())));
	}

	private SimulationDays newBaseInputSpecification() {
		return newSimulationDays(days);
	}

	private SimulationDays newSimulationDays(int days) {
		return SimulationDays.containing(days);
	}
}
