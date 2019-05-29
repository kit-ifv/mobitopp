package edu.kit.ifv.mobitopp.simulation.person;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.time.Time;

public class CarSharingStationTripTest {

  private TripSetup setup;
  private ImpedanceIfc impedance;
  private SimulationPerson person;
  private Time currentTime;
  private TripData trip;
  private Zone zone;
  private Location location;
  private PersonListener listener;
  private CarSharingCar car;
  private CarSharingDataForZone carSharingData;

  @BeforeEach
  public void initialise() {
    setup = TripSetup.create();
    impedance = setup.impedance;
    person = setup.person;
    trip = setup.tripData;
    zone = setup.zone;
    location = setup.location;
    currentTime = setup.currentTime;
    listener = setup.results;
    car = mock(CarSharingCar.class);
    carSharingData = mock(CarSharingDataForZone.class);
    zone.setCarSharing(carSharingData);
    when(trip.mode()).thenReturn(Mode.CARSHARING_STATION);
    when(car.id()).thenReturn(0);
  }

  @Test
  void failsForNullArguments() throws Exception {
    CarSharingStationTrip carSharingTrip = new CarSharingStationTrip(trip, person);
    
    assertAll(
        () -> assertThrows(NullPointerException.class,
            () -> carSharingTrip.prepareTrip(null, currentTime)),
        () -> assertThrows(NullPointerException.class,
            () -> carSharingTrip.prepareTrip(impedance, null)));
  }
  
  @Test
  void allocateVehicle() throws Exception {
    CarSharingCar carSharingCar = mock(CarSharingCar.class);
    setup.configureCurrentActivity(ActivityType.HOME);
    when(carSharingData.isStationBasedCarSharingCarAvailable(person)).thenReturn(true);
    when(carSharingData.bookStationBasedCar(person)).thenReturn(carSharingCar);
    CarSharingStationTrip carSharingTrip = new CarSharingStationTrip(trip, person);

    carSharingTrip.prepareTrip(impedance, currentTime);

    verify(person).useCar(carSharingCar, currentTime);
    verify(carSharingData).bookStationBasedCar(person);
  }

  @Test
  void usesParkedCar() throws Exception {
    when(person.hasParkedCar()).thenReturn(true);
    when(person.isCarDriver()).thenReturn(false);
    when(person.whichCar()).thenReturn(car);
    setup.configureCurrentActivity(ActivityType.WORK);
    Trip carTrip = new CarSharingStationTrip(trip, person);

    carTrip.prepareTrip(impedance, currentTime);

    verify(person).takeCarFromParking();
  }

  @Test
  void parkCarAtWork() throws Exception {
    setup.configureCurrentActivity(ActivityType.HOME);
    setup.configureNextActivity(ActivityType.WORK);
    when(person.isCarDriver()).thenReturn(true);
    when(person.whichCar()).thenReturn(car);
    when(person.parkCar(zone, location, currentTime)).thenReturn(car);

    Trip privateCarTrip = new CarSharingStationTrip(trip, person);

    FinishedTrip finishedTrip = privateCarTrip.finish(currentTime, listener);

    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(car.id())));
    verify(person).parkCar(zone, location, currentTime);
  }

  @Test
  void returnCarAtHome() throws Exception {
    setup.configureCurrentActivity(ActivityType.WORK);
    setup.configureNextActivity(ActivityType.HOME);
    when(person.isCarDriver()).thenReturn(true);
    when(person.whichCar()).thenReturn(car);
    when(person.releaseCar(currentTime)).thenReturn(car);

    Trip privateCarTrip = new CarSharingStationTrip(trip, person);

    FinishedTrip finishedTrip = privateCarTrip.finish(currentTime, listener);

    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(car.id())));
    verify(person).releaseCar(currentTime);
    verify(car).returnCar(zone);
    verify(car).stop(eq(currentTime), any());
  }
  
  @Test
  void failsWhenFinishingACarTripWithoutBeingADriver() throws Exception {
    when(person.isCarDriver()).thenReturn(false);
    Trip carSharingTrip = new CarSharingStationTrip(trip, person);

    assertThrows(IllegalStateException.class, () -> carSharingTrip.finish(currentTime, listener));
  }
}
