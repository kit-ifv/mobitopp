package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

public class MultipleResultsTest {

  @MethodSource("methods")
  @ParameterizedTest
  void delegatesCallsToAllListeners(Consumer<PersonListener> method) throws Exception {
    PersonListener someListener = mock(PersonListener.class);
    PersonListener otherListener = mock(PersonListener.class);
    MultipleResults results = new MultipleResults();
    results.addListener(someListener);
    results.addListener(otherListener);

    method.accept(results);

    method.accept(verify(someListener));
    method.accept(verify(otherListener));
  }

  static Stream<Consumer<PersonListener>> methods() {
    Person person = mock(Person.class);
    Car car = mock(Car.class);
    FinishedTrip trip = mock(FinishedTrip.class);
    ActivityIfc activity = mock(ActivityIfc.class);
    TripData tripData = mock(TripData.class);
    Path route = mock(Path.class);
    StateChange stateChange = mock(StateChange.class);
    Tour tour = mock(Tour.class);
    Subtour subtour = mock(Subtour.class);
    Mode tourMode = StandardMode.CAR;
    Zone tourDestination = ExampleZones.create().someZone();
    return Stream
        .of(l -> l.notifyEndTrip(person, trip, activity),
            l -> l.notifyFinishCarTrip(person, car, trip, activity),
            l -> l.notifyStartActivity(person, activity),
            l -> l.notifySelectCarRoute(person, car, tripData, route),
            l -> l.notifyStateChanged(stateChange), l -> l.notifyFinishSimulation(),
            l -> l.writeSubourinfoToFile(person, tour, subtour, tourMode),
            l -> l.writeTourinfoToFile(person, subtour, tourDestination, tourMode));
  }
}
