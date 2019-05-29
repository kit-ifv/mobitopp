package edu.kit.ifv.mobitopp.simulation.person;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
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

public class CarSharingFreeFloatingTripTest {

  private TripSetup setup;
  private ImpedanceIfc impedance;
  private SimulationPerson person;
  private Time currentTime;
  private TripData data;
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
    data = setup.tripData;
    zone = setup.zone;
    location = setup.location;
    currentTime = setup.currentTime;
    listener = setup.results;
    car = mock(CarSharingCar.class);
    carSharingData = mock(CarSharingDataForZone.class);
    zone.setCarSharing(carSharingData);
    when(data.mode()).thenReturn(Mode.CARSHARING_FREE);
    when(car.id()).thenReturn(0);
  }

  @Test
  void failsForNullArguments() throws Exception {
    CarSharingFreeFloatingTrip carSharingTrip = newTrip();
    
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
    when(person.isCarDriver()).thenReturn(false);
    when(carSharingData.isFreeFloatingCarSharingCarAvailable(person)).thenReturn(true);
    when(carSharingData.bookFreeFloatingCar(person)).thenReturn(carSharingCar);
    CarSharingFreeFloatingTrip carSharingTrip = newTrip();

    carSharingTrip.prepareTrip(impedance, currentTime);

    verify(person).useCar(carSharingCar, currentTime);
    verify(carSharingData).bookFreeFloatingCar(person);
  }

  @Test
  void returnCarInFreeFloatingArea() throws Exception {
    setup.configureNextActivity(ActivityType.HOME);
    configureMode();
    configureCarUsage();
    configureFreeFloatingZone(true);

    Trip privateCarTrip = newTrip();

    FinishedTrip finishedTrip = privateCarTrip.finish(currentTime, listener);

    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(car.id())));
    verify(person).releaseCar(currentTime);
    verify(car).returnCar(zone);
    verify(car).stop(eq(currentTime), any());
  }

  @Test
  void doNotReturnCarOutOfFreeFloatingArea() throws Exception {
    setup.configureNextActivity(ActivityType.HOME);
    configureMode();
    configureCarUsage();
    configureParkedCar();
    configureFreeFloatingZone(false);

    Trip privateCarTrip = newTrip();

    FinishedTrip finishedTrip = privateCarTrip.finish(currentTime, listener);

    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(car.id())));
    verify(person, atLeastOnce()).isCarDriver();
    verify(person).parkCar(zone, location, currentTime);
    verify(car).stop(eq(currentTime), any());
  }

  public void configureParkedCar() {
    when(person.parkCar(zone, location, currentTime)).thenReturn(car);
  }
  
  @Test
  void failsWhenFinishingACarTripWithoutBeingADriver() throws Exception {
    when(person.isCarDriver()).thenReturn(false);
    Trip carSharingTrip = newTrip();

    assertThrows(IllegalStateException.class, () -> carSharingTrip.finish(currentTime, listener));
  }

  private CarSharingFreeFloatingTrip newTrip() {
    return new CarSharingFreeFloatingTrip(data, person);
  }

  private void configureFreeFloatingZone(boolean value) {
    when(carSharingData.isFreeFloatingZone(car)).thenReturn(value);
  }

  private void configureMode() {
    when(data.mode()).thenReturn(Mode.CARSHARING_FREE);
  }

  private void configureCarUsage() {
    when(person.isCarDriver()).thenReturn(true);
    when(person.whichCar()).thenReturn(car);
    when(person.releaseCar(currentTime)).thenReturn(car);
  }
}
