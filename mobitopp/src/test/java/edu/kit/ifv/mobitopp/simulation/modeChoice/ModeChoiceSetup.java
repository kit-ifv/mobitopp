package edu.kit.ifv.mobitopp.simulation.modeChoice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public class ModeChoiceSetup {

  final ImpedanceIfc impedance;
  final SimulationPerson person;
  final Time currentTime;
  final PrivateCar car;
  final Zone origin;
  final Zone destination;
  final Location originLocation;
  final Location destinationLocation;
  final FinishedTrip finishedSuper;
  final PersonResults results;

  private ModeChoiceSetup() {
    super();
    currentTime = Time.start;
    impedance = mock(ImpedanceIfc.class);
    person = mock(SimulationPerson.class);
    car = mock(PrivateCar.class);
    finishedSuper = mock(FinishedTrip.class);
    results = mock(PersonResults.class);
    origin = ExampleZones.create().someZone();
    destination = ExampleZones.create().otherZone();
    originLocation = origin.centroidLocation();
    destinationLocation = destination.centroidLocation();
  }

  private void initialise() {
    Household household = mock(Household.class);
    float distance = 30000.0f;
    float distanceKm = distance / 1000.0f;
    when(household.takeAvailableCar(person, distanceKm)).thenReturn(car);
    when(person.household()).thenReturn(household);
    when(impedance.getDistance(any(), any())).thenReturn(distance);
    origin.setCarSharing(mock(CarSharingDataForZone.class));
    destination.setCarSharing(mock(CarSharingDataForZone.class));
  }

  public void configureCurrentActivity(ActivityType type) {
    ActivityIfc activity = createActivity(type, origin);
    when(person.currentActivity()).thenReturn(activity);
  }
  
  public void configureNextActivity(ActivityType type) {
    ActivityIfc activity = createActivity(type, destination);
    when(person.nextActivity()).thenReturn(activity);
  }

  public ActivityIfc createActivity(ActivityType type, Zone zone) {
    ActivityIfc activity = mock(ActivityIfc.class);
    when(activity.activityType()).thenReturn(type);
    when(activity.zone()).thenReturn(zone);
    when(activity.location()).thenReturn(zone.centroidLocation());
    return activity;
  }

  public static ModeChoiceSetup create() {
    ModeChoiceSetup setup = new ModeChoiceSetup();
    setup.initialise();
    return setup;
  }
}
