package edu.kit.ifv.mobitopp.simulation.person;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.time.Time;

public class PrivateCarTripTest {

  private TripSetup setup;
  private ImpedanceIfc impedance;
  private SimulationPerson person;
  private Time currentTime;
  private PrivateCar car;
  private Zone zone;
  private TripData trip;
  private Location location;
  private PersonResults results;

  @BeforeEach
  public void initialise() {
    setup = TripSetup.create();
    impedance = setup.impedance;
    person = setup.person;
    car = setup.car;
    trip = setup.tripData;
    zone = setup.zone;
    location = setup.location;
    currentTime = setup.currentTime;
    results = setup.results;
    when(trip.mode()).thenReturn(StandardMode.CAR);
  }

  @Test
  void allocateVehicle() throws Exception {
    setup.configureCurrentActivity(ActivityType.HOME);
    Trip carTrip = new PrivateCarTrip(trip, person);

    carTrip.prepareTrip(impedance, currentTime);

    verify(person).useCar(car, currentTime);
  }

  @Test
  void usesParkedCar() throws Exception {
    when(person.hasParkedCar()).thenReturn(true);
    when(person.isCarDriver()).thenReturn(false);
    when(person.whichCar()).thenReturn(car);
    setup.configureCurrentActivity(ActivityType.WORK);
    Trip carTrip = new PrivateCarTrip(trip, person);

    carTrip.prepareTrip(impedance, currentTime);

    verify(person).takeCarFromParking();
  }

  @Test
  void parkCarAtWork() throws Exception {
    setup.configureCurrentActivity(ActivityType.HOME);
    ActivityIfc nextActivity = setup.configureNextActivity(ActivityType.WORK);
    when(person.isCarDriver()).thenReturn(true);
    when(person.whichCar()).thenReturn(car);
    when(person.parkCar(zone, location, currentTime)).thenReturn(car);

    Trip privateCarTrip = new PrivateCarTrip(trip, person);

    FinishedTrip finishedTrip = privateCarTrip.finish(currentTime, results);

    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(car.id())));
    verify(person).parkCar(zone, location, currentTime);
    verify(results).notifyFinishCarTrip(eq(person), eq(car), any(), eq(nextActivity));
  }

  @Test
  void returnCarAtHome() throws Exception {
    setup.configureCurrentActivity(ActivityType.WORK);
    ActivityIfc nextActivity = setup.configureNextActivity(ActivityType.HOME);
    when(person.isCarDriver()).thenReturn(true);
    when(person.whichCar()).thenReturn(car);
    when(person.releaseCar(currentTime)).thenReturn(car);

    Trip privateCarTrip = new PrivateCarTrip(trip, person);

    FinishedTrip finishedTrip = privateCarTrip.finish(currentTime, results);

    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(car.id())));
    verify(person).releaseCar(currentTime);
    verify(car).returnCar(zone);
    verify(car).stop(eq(currentTime), any());
    verify(results).notifyFinishCarTrip(eq(person), eq(car), any(), eq(nextActivity));
  }

  @Test
  void failsWhenFinishingACarTripWithoutBeingADriver() throws Exception {
    when(person.isCarDriver()).thenReturn(false);
    PrivateCarTrip privateCarTrip = new PrivateCarTrip(trip, person);

    assertThrows(IllegalStateException.class, () -> privateCarTrip.finish(currentTime, results));
  }

}
