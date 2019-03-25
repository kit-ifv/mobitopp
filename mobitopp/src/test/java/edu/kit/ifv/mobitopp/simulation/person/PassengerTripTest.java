package edu.kit.ifv.mobitopp.simulation.person;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.time.Time;

public class PassengerTripTest {

  private TripSetup setup;
  private SimulationPerson person;
  private Time currentTime;
  private PrivateCar car;
  private TripData trip;
  private PersonListener listener;

  @BeforeEach
  public void initialise() {
    setup = TripSetup.create();
    person = setup.person;
    car = setup.car;
    trip = setup.tripData;
    currentTime = setup.currentTime;
    listener = setup.results;
  }

  @Test
  void addsCarIdWhenUserIsPassenger() throws Exception {
    configureBeingPassenger(true);
    int carId = 1;
    configureUsedCar(carId);
    PassengerTrip passengerTrip = newTrip();

    FinishedTrip finishedTrip = passengerTrip.finish(currentTime, listener);

    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(carId)));
    verify(person).leaveCar();
  }

  private void configureBeingPassenger(boolean value) {
    when(person.isCarPassenger()).thenReturn(value);
  }

  private void configureUsedCar(int carId) {
    when(car.id()).thenReturn(carId);
    when(person.whichCar()).thenReturn(car);
  }

  @Test
  void addsCarIdWhenUserIsNoPassenger() throws Exception {
    configureBeingPassenger(false);
    PassengerTrip passengerTrip = newTrip();

    FinishedTrip finishedTrip = passengerTrip.finish(currentTime, listener);

    assertThat(finishedTrip.vehicleId(), hasValue("-1"));
  }

  private PassengerTrip newTrip() {
    return new PassengerTrip(trip, person);
  }
}
