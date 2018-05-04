package edu.kit.ifv.mobitopp.simulation.publictransport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class VehicleLocationTest {

	@Test
	public void movesFromStopToStop() {
		Route route = route();
		VehicleLocation location = new VehicleLocation(route);

		Stop start = location.current();
		location.move();
		Stop end = location.current();
		
		assertThat(start, is(equalTo(Data.someStop())));
		assertThat(end, is(equalTo(Data.anotherStop())));
	}

	private Route route() {
		List<Stop> stops = Arrays.asList(Data.someStop(), Data.anotherStop());
		return new Route(stops);
	}
}
