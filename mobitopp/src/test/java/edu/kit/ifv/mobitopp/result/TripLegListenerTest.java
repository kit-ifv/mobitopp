package edu.kit.ifv.mobitopp.result;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.StateChange;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

public class TripLegListenerTest {

	@ParameterizedTest(name = "method: {0}")
	@MethodSource("delegatedMethods")
	void delegateToOther(String name, Consumer<PersonListener> method) throws Exception {
		PersonListener other = mock(PersonListener.class);
		TripLegListener listener = new TripLegListener(other);

		method.accept(listener);

		method.accept(verify(other));
	}

	private static Stream<Arguments> delegatedMethods() {
		Person person = mock(Person.class);
		FinishedTrip trip = mock(FinishedTrip.class);
		StartedTrip startedTrip = mock(StartedTrip.class);
		Car car = mock(Car.class);
		ActivityIfc activity = mock(ActivityIfc.class);
		TripData tripData = mock(TripData.class);
		Path route = mock(Path.class);
		Tour tour = mock(Tour.class);
		Subtour subtour = mock(Subtour.class);
		Mode tourMode = StandardMode.CAR;
		Zone tourDestination = mock(Zone.class);
		StateChange stateChange = mock(StateChange.class);
		return Stream.of(
				Arguments.of("FinishCarTrip", (Consumer<PersonListener>) listener -> listener.notifyFinishCarTrip(person, car, trip, activity)),
				Arguments.of("StartActivity", (Consumer<PersonListener>) listener -> listener.notifyStartActivity(person, activity)),
				Arguments.of("SelectCarRoute", (Consumer<PersonListener>) listener -> listener.notifySelectCarRoute(person, car, tripData, route)),
				Arguments.of("SubtourinfoToFile", (Consumer<PersonListener>) listener -> listener.writeSubtourinfoToFile(person, tour, subtour, tourMode)),
				Arguments.of("TourinfoToFile", (Consumer<PersonListener>) listener -> listener.writeTourinfoToFile(person, tour, tourDestination, tourMode)),
				Arguments.of("StateChange", (Consumer<PersonListener>) listener -> listener.notifyStateChanged(stateChange )),
				Arguments.of("FinishSimulation", (Consumer<PersonListener>) listener -> listener.notifyFinishSimulation()));
	}
	
	@Test
	void notifyEndOfEachLeg() throws Exception {
		Person person = mock(Person.class);
		FinishedTrip trip = mock(FinishedTrip.class);
		PersonListener other = mock(PersonListener.class);
		TripLegListener listener = new TripLegListener(other);

		listener.notifyEndTrip(person, trip);
		
		verify(trip).forEachFinishedLeg(any());
	}
	
	@Test
	void notifyStartOfEachLeg() throws Exception {
		Person person = mock(Person.class);
		StartedTrip trip = mock(StartedTrip.class);
		PersonListener other = mock(PersonListener.class);
		TripLegListener listener = new TripLegListener(other);

		listener.notifyStartTrip(person, trip);
		
		verify(trip).forEachStartedLeg(any());
	}

}
