package edu.kit.ifv.mobitopp.simulation;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.person.BaseStartedTrip;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.time.Time;

public class AggregateDemandOfStartedTripTest {

	private List<ZoneId> oids;
	private Consumer<IntegerMatrix> writer;
	private Person person;
	private BaseStartedTrip trip;
	private int matrixColumn;

	@SuppressWarnings("unchecked")
	@BeforeEach
	void setUp() throws Exception {
		writer = mock(Consumer.class);

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
		BiPredicate<Person, StartedTrip<?>> filter = AggregateDemandFactory
				.dayOfWeekFilter(DayOfWeek.MONDAY);
		AggregateDemandOfStartedTrips aggregateDemand = new AggregateDemandOfStartedTrips(oids,
				filter, writer, 1);

		aggregateDemand.notifyStartTrip(person, trip);
		aggregateDemand.writeMatrix();

		IntegerMatrix matrix = new IntegerMatrix(oids);
		matrix.set(matrixColumn, matrixColumn, 1);
		verify(writer).accept(matrix);
	}

	@Test
	void aggregateFilteredTrips() {

		BiPredicate<Person, StartedTrip<?>> filter = AggregateDemandFactory
				.dayOfWeekFilter(DayOfWeek.TUESDAY);
		AggregateDemandOfStartedTrips aggregateDemand = new AggregateDemandOfStartedTrips(oids,
				filter, writer, 1);

		aggregateDemand.notifyStartTrip(person, trip);
		aggregateDemand.writeMatrix();

		IntegerMatrix matrix = new IntegerMatrix(oids);
		matrix.set(matrixColumn, matrixColumn, 0);
		verify(writer).accept(matrix);
	}

	@Test
	void aggregateScaledTrips() {

		BiPredicate<Person, StartedTrip<?>> filter = AggregateDemandFactory
				.dayOfWeekFilter(DayOfWeek.MONDAY);
		AggregateDemandOfStartedTrips aggregateDemand = new AggregateDemandOfStartedTrips(oids,
				filter, writer, 42);

		aggregateDemand.notifyStartTrip(person, trip);
		aggregateDemand.writeMatrix();

		IntegerMatrix matrix = new IntegerMatrix(oids);
		matrix.set(matrixColumn, matrixColumn, 42);
		verify(writer).accept(matrix);
	}

}
