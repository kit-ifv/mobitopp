package edu.kit.ifv.mobitopp.simulation.person;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
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
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
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
  private TripIfc trip;
  private Location location;

  @BeforeEach
  public void initialise() {
    setup = TripSetup.create();
    impedance = setup.impedance;
    person = setup.person;
    car = setup.car;
    trip = setup.trip;
    zone = setup.zone;
    location = setup.location;
    currentTime = setup.currentTime;
    when(trip.mode()).thenReturn(Mode.CAR);
  }

  @Test
  void allocateVehicle() throws Exception {
    setup.configureActivity(ActivityType.HOME);
    TripIfc carTrip = new PrivateCarTrip(trip, person);

    carTrip.allocateVehicle(impedance, currentTime);

    verify(person).useCar(car, currentTime);
  }

  @Test
  void usesParkedCar() throws Exception {
    when(person.hasParkedCar()).thenReturn(true);
    when(person.isCarDriver()).thenReturn(false);
    setup.configureActivity(ActivityType.WORK);
    TripIfc carTrip = new PrivateCarTrip(trip, person);

    carTrip.allocateVehicle(impedance, currentTime);

    verify(person).takeCarFromParking();
  }

  @Test
  void parkCarAtWork() throws Exception {
    FinishedTrip finishedSuper = mock(FinishedTrip.class);
    PersonResults results = mock(PersonResults.class);
    setup.configureActivity(ActivityType.HOME);
    ActivityIfc nextActivity = setup.createActivity(ActivityType.WORK);
    when(trip.nextActivity()).thenReturn(nextActivity);
    when(trip.finish(currentTime, results)).thenReturn(finishedSuper);
    when(person.isCarDriver()).thenReturn(true);
    when(person.whichCar()).thenReturn(car);
    when(person.parkCar(zone, location, currentTime)).thenReturn(car);
    
    TripIfc privateCarTrip = new PrivateCarTrip(trip, person);

    FinishedTrip finishedTrip = privateCarTrip.finish(currentTime, results);

    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(car.id())));
    verify(person).parkCar(zone, location, currentTime);
    verify(results).notifyFinishCarTrip(person, car, trip, nextActivity);
  }
  
  @Test
  void returnCarAtHome() throws Exception {
    FinishedTrip finishedSuper = mock(FinishedTrip.class);
    PersonResults results = mock(PersonResults.class);
    setup.configureActivity(ActivityType.WORK);
    ActivityIfc nextActivity = setup.createActivity(ActivityType.HOME);
    when(trip.nextActivity()).thenReturn(nextActivity);
    when(trip.finish(currentTime, results)).thenReturn(finishedSuper);
    when(person.isCarDriver()).thenReturn(true);
    when(person.whichCar()).thenReturn(car);
    when(person.releaseCar(currentTime)).thenReturn(car);
    
    TripIfc privateCarTrip = new PrivateCarTrip(trip, person);
    
    FinishedTrip finishedTrip = privateCarTrip.finish(currentTime, results);
    
    assertThat(finishedTrip.vehicleId(), hasValue(String.valueOf(car.id())));
    verify(person).releaseCar(currentTime);
    verify(car).returnCar(zone);
    verify(results).notifyFinishCarTrip(person, car, trip, nextActivity);
  }
  
}
