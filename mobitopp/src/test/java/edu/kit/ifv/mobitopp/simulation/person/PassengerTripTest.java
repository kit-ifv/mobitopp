package edu.kit.ifv.mobitopp.simulation.person;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.time.Time;

public class PassengerTripTest {

  private TripSetup setup;
  private SimulationPerson person;
  private Time currentTime;
  private PrivateCar car;
  private TripIfc trip;
  private PersonResults results;

  @BeforeEach
  public void initialise() {
    setup = TripSetup.create();
    person = setup.person;
    car = setup.car;
    trip = setup.trip;
    currentTime = setup.currentTime;
    results = setup.results;
  }

  @Test
  void addsCarIdWhenUserIsPassenger() throws Exception {
    when(person.isCarPassenger()).thenReturn(true);
    int carId = 1;
    when(car.id()).thenReturn(carId);
    when(person.whichCar()).thenReturn(car);
    PassengerTrip passengerTrip = new PassengerTrip(trip, person);

    FinishedTrip finishedTrip = passengerTrip.finish(currentTime, results);

    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(carId)));
    verify(person).leaveCar();
  }

  @Test
  void addsCarIdWhenUserIsNoPassenger() throws Exception {
    when(person.isCarPassenger()).thenReturn(false);
    PassengerTrip passengerTrip = new PassengerTrip(trip, person);

    FinishedTrip finishedTrip = passengerTrip.finish(currentTime, results);

    assertThat(finishedTrip.vehicleId(), hasValue("-1"));
  }
}
