package edu.kit.ifv.mobitopp.simulation;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.person.BaseStartedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.Setter;

public class AggregateDemandOfStartedTripTest {

	private List<ZoneId> oids;
	private Person person;
	private BaseStartedTrip trip;
	private int matrixColumn;
	private TestData data;

	@BeforeEach
	void setUp() throws Exception {
		data = new TestData();

		matrixColumn = 0;
		ZoneId zoneId = new ZoneId("11", matrixColumn);
		oids = asList(zoneId);

		person = mock(Person.class);
		trip = mock(BaseStartedTrip.class);

		Zone zone = mock(Zone.class);
		when(zone.getId()).thenReturn(zoneId);
		Location location = new Location(new Point2D.Double(), 0, 0);
		ZoneAndLocation zoneLocation = new ZoneAndLocation(zone, location);
		when(trip.origin()).thenReturn(zoneLocation);
		when(trip.destination()).thenReturn(zoneLocation);
		when(trip.startDate()).thenReturn(Time.start);
	}

	@Test
	void aggregateNotFilteredTrips() {
		BiPredicate<Person, StartedTrip> filter = AggregateDemandFactory
				.dayOfWeekFilter(DayOfWeek.MONDAY);
		AggregateDemandOfStartedTrips aggregateDemand = new AggregateDemandOfStartedTrips(oids,
				filter, data::setMatrix, 1);

		aggregateDemand.notifyStartTrip(person, trip);
		aggregateDemand.writeMatrix();

		assertThat(data.matrix.get(matrixColumn, matrixColumn)).isEqualTo(1);
	}

	@Test
	void aggregateFilteredTrips() {

		BiPredicate<Person, StartedTrip> filter = AggregateDemandFactory
				.dayOfWeekFilter(DayOfWeek.TUESDAY);
		AggregateDemandOfStartedTrips aggregateDemand = new AggregateDemandOfStartedTrips(oids,
				filter, data::setMatrix, 1);

		aggregateDemand.notifyStartTrip(person, trip);
		aggregateDemand.writeMatrix();

		assertThat(data.matrix.get(matrixColumn, matrixColumn)).isEqualTo(0);
	}

	@Test
	void aggregateScaledTrips() {

		BiPredicate<Person, StartedTrip> filter = AggregateDemandFactory
				.dayOfWeekFilter(DayOfWeek.MONDAY);
		AggregateDemandOfStartedTrips aggregateDemand = new AggregateDemandOfStartedTrips(oids,
				filter, data::setMatrix, 42);

		aggregateDemand.notifyStartTrip(person, trip);
		aggregateDemand.writeMatrix();

		assertThat(data.matrix.get(matrixColumn, matrixColumn)).isEqualTo(42);
	}

	@Setter
	private final class TestData {

		private IntegerMatrix matrix;

	}

}
