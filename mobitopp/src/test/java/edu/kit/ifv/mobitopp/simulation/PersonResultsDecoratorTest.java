package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.stubbing.Answer;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

public class PersonResultsDecoratorTest {

  private PersonResults other;
  private PersonResultsDecorator decorator;

  @BeforeEach
  public void initialise() {
    other = mock(PersonResults.class);

    decorator = new PersonResultsDecorator(other);
  }

  @MethodSource("methods")
  @ParameterizedTest
  void delegatesToOther(Consumer<PersonResults> method) throws Exception {
    method.accept(decorator);

    method.accept(verify(other));
  }

  static Stream<Consumer<PersonResults>> methods() {
    Person person = createMock(Person.class);
    Car car = createMock(Car.class);
    FinishedTrip trip = createMock(FinishedTrip.class);
    ActivityIfc activity = createMock(ActivityIfc.class);
    TripData tripData = createMock(TripData.class);
    Path route = createMock(Path.class);
    Tour tour = createMock(Tour.class);
    Subtour subtour = createMock(Subtour.class);
    Mode tourMode = Mode.CAR;
    Zone tourDestination = ExampleZones.create().someZone();
    StateChange change = createMock(StateChange.class);
    return Stream
        .of(results -> results.notifyEndTrip(person, trip, activity),
            results -> results.notifyFinishCarTrip(person, car, trip, activity),
            results -> results.notifyStartActivity(person, activity),
            results -> results.notifySelectCarRoute(person, car, tripData, route),
            results -> results.writeSubourinfoToFile(person, tour, subtour, tourMode),
            results -> results.writeTourinfoToFile(person, tour, tourDestination, tourMode),
            results -> results.notifyStateChanged(change),
            results -> results.close());
  }

  private static <T> T createMock(Class<T> classToMock) {
    return mock(classToMock, failOnMethodCall());
  }

  private static Answer<?> failOnMethodCall() {
    return i -> {
      throw new RuntimeException("No methods should be called");
    };
  }
}
