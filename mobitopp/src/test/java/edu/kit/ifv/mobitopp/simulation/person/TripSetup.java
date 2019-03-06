package edu.kit.ifv.mobitopp.simulation.person;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.time.Time;

public class TripSetup {

  final ImpedanceIfc impedance;
  final SimulationPerson person;
  final Time currentTime;
  final PrivateCar car;
  final TripData tripData;
  final Zone zone;
  final Location location;
  final FinishedTrip finishedSuper;
  final PersonResults results;

  private TripSetup() {
    super();
    currentTime = Time.start;
    impedance = mock(ImpedanceIfc.class);
    person = mock(SimulationPerson.class);
    car = mock(PrivateCar.class);
    tripData = mock(TripData.class);
    finishedSuper = mock(FinishedTrip.class);
    results = mock(PersonResults.class);
    zone = ExampleZones.create().someZone();
    location = zone.centroidLocation();
  }

  private void initialise() {
    Household household = mock(Household.class);
    Location location = zone.centroidLocation();
    ZoneAndLocation zoneAndLocation = new ZoneAndLocation(zone, location);
    float distance = 30000.0f;
    float distanceKm = distance / 1000.0f;
    when(household.takeAvailableCar(person, distanceKm)).thenReturn(car);
    when(person.household()).thenReturn(household);
    when(impedance.getDistance(anyInt(), anyInt())).thenReturn(distance);
    when(tripData.origin()).thenReturn(zoneAndLocation);
    when(tripData.destination()).thenReturn(zoneAndLocation);
  }

  public static TripSetup create() {
    TripSetup setup = new TripSetup();
    setup.initialise();
    return setup;
  }

  public void configureCurrentActivity(ActivityType type) {
    ActivityIfc activity = createActivity(type);
    when(person.currentActivity()).thenReturn(activity);
    when(tripData.previousActivity()).thenReturn(activity);
  }

  public ActivityIfc createActivity(ActivityType type) {
    ActivityIfc activity = mock(ActivityIfc.class);
    when(activity.activityType()).thenReturn(type);
    when(activity.zone()).thenReturn(zone);
    when(activity.location()).thenReturn(zone.centroidLocation());
    return activity;
  }

  public ActivityIfc configureNextActivity(ActivityType type) {
    ActivityIfc nextActivity = createActivity(type);
    when(tripData.nextActivity()).thenReturn(nextActivity);
    return nextActivity;
  }
}
