package edu.kit.ifv.mobitopp.simulation.car;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;

public class ConventionalCarTest {

  @Test
  void useCar() throws Exception {
    int capacity = 2;
    Car car = newCar(capacity);

    Person person = mock(Person.class);
    Time time = Time.start;
    car.use(person, time);

    assertAll(() -> assertTrue(car.isUsed(), "isUsed"),
        () -> assertThat(car.driver(), is(equalTo(person))),
        () -> assertThat(car.startOfLastUsage(), is(equalTo(time))),
        () -> assertTrue(car.canCarryPassengers(), "canCarryPassengers"));

  }

  @Test
  void useAsPassenger() throws Exception {
    int capacity = 2;
    ConventionalCar car = newCar(capacity);
    Person driver = mock(Person.class);
    Person somePerson = mock(Person.class);

    car.use(driver, Time.start);
    car.useAsPassenger(somePerson);

    assertAll(() -> assertThat(car.passengers, contains(somePerson)),
        () -> assertFalse(car.canCarryPassengers(), "canCarryPassengers"));
  }

  private ConventionalCar newCar(int capacity) {
    Zone someZone = ExampleZones.create().someZone();
    Location someLocation = someZone.centroidLocation();
    return new ConventionalCar(1, new CarPosition(someZone, someLocation), Segment.SMALL, capacity,
        0.0f, 1.0f, 100);
  }
}
