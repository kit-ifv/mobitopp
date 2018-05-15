package edu.kit.ifv.mobitopp.simulation;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.someTime;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.BeamedTrip;

public class StatisticResultsTest {

	private PersonResults other;
	private StatisticResults statistic;
	private Person person;
	private TripIfc trip;
	private BeamedTrip finishedTrip;
	private ActivityIfc activity;
	private Car car;
	private Path route;

	@Before
	public void initialise() {
		other = mock(PersonResults.class);
		person = mock(Person.class);
		trip = mock(TripIfc.class);
		finishedTrip = new BeamedTrip(trip, someTime());
		car = mock(Car.class);
		activity = mock(ActivityIfc.class);
		route = mock(Path.class);

		statistic = new StatisticResults(other);
	}
	
	@Test
	public void collectsTrips() {
		statistic.notifyEndTrip(person, finishedTrip, activity);
		
		assertThat(statistic.trips(), contains(finishedTrip));
	}

	@Test
	public void delegatesEndTripNotification() {
		statistic.notifyEndTrip(person, finishedTrip, activity);

		verify(other).notifyEndTrip(person, finishedTrip, activity);
		verifyNoMoreInteractions(other);
	}

	@Test
	public void delegatesFinishCarTripNotification() {
		statistic.notifyFinishCarTrip(person, car, trip, activity);

		verify(other).notifyFinishCarTrip(person, car, trip, activity);
		verifyNoMoreInteractions(other);
	}

	@Test
	public void delegatesStartActivityNotification() {
		statistic.notifyStartActivity(person, activity);

		verify(other).notifyStartActivity(person, activity);
		verifyNoMoreInteractions(other);
	}

	@Test
	public void delegatesSelectCarRouteNotification() {
		statistic.notifySelectCarRoute(person, car, trip, route);

		verify(other).notifySelectCarRoute(person, car, trip, route);
		verifyNoMoreInteractions(other);
	}
}
