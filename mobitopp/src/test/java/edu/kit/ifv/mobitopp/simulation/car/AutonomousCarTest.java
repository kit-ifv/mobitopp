package edu.kit.ifv.mobitopp.simulation.car;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;

public class AutonomousCarTest {

  @Test
  void hasNoDriver() throws Exception {
    Car car = newCar();

    assertAll(() -> assertFalse(car.hasDriver()),
        () -> assertThrows(IllegalStateException.class, car::driver));

  }

  @Test
  void hasCapacity() throws Exception {
    assertTrue(newCar().hasCapacity());
  }

  @Test
  void useAsPassenger() throws Exception {
    int capacity = 1;
    Person person = mock(Person.class);

    Car car = newCar(capacity);

    car.useAsPassenger(person);

    assertAll(() -> assertFalse(car.hasCapacity()),
        () -> assertThat(car.remainingCapacity(), is(0)),
        () -> assertFalse(car.canCarryPassengers()));
  }

  private AutonomousCar newCar() {
    int capacity = 1;
    return newCar(capacity);
  }

  private AutonomousCar newCar(int capacity) {
    Zone someZone = ExampleZones.create().someZone();
    Location someLocation = someZone.centroidLocation();
    return new AutonomousCar(1, new CarPosition(someZone, someLocation), Segment.SMALL, capacity,
        0.0f, 1.0f, 100);
  }
}
