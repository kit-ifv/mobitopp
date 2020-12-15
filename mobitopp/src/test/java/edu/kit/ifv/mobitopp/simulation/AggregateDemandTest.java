package edu.kit.ifv.mobitopp.simulation;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import lombok.Setter;

public class AggregateDemandTest {

	@Test
	void aggregateTrips() throws Exception {
		TestData data = new TestData();

		int matrixColumn = 0;
		ZoneId zoneId = new ZoneId("11", matrixColumn);
		List<ZoneId> oids = asList(zoneId);
		AggregateDemand aggregateDemand = new AggregateDemand(data::setMatrix, oids);

		Person person = mock(Person.class);
		FinishedTrip trip = mock(FinishedTrip.class);
		Zone zone = mock(Zone.class);
		when(zone.getId()).thenReturn(zoneId);
		Location location = new Location(new Point2D.Double(), 0, 0);
		ZoneAndLocation zoneLocation = new ZoneAndLocation(zone, location);
		when(trip.origin()).thenReturn(zoneLocation);
		when(trip.destination()).thenReturn(zoneLocation);

		aggregateDemand.notifyEndTrip(person, trip);
		aggregateDemand.notifyFinishSimulation();

		assertThat(data.matrix.get(matrixColumn, matrixColumn)).isEqualTo(1);
	}
	
	@Setter
	private final class TestData {

		private IntegerMatrix matrix;

	}


}
