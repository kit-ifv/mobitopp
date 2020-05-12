package edu.kit.ifv.mobitopp.simulation.bikesharing;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
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

  public static TripSetup create() {
    return new TripSetup();
  }

  public void configureCurrentActivity(ActivityType type) {
    ActivityIfc activity = createActivity(type);
    when(person.currentActivity()).thenReturn(activity);
  }

  public ActivityIfc createActivity(ActivityType type) {
    ActivityIfc activity = mock(ActivityIfc.class);
    lenient().when(activity.activityType()).thenReturn(type);
    when(activity.zone()).thenReturn(zone);
    lenient().when(activity.location()).thenReturn(zone.centroidLocation());
    return activity;
  }

  public ActivityIfc configureNextActivity(ActivityType type) {
    ActivityIfc nextActivity = createActivity(type);
    when(tripData.nextActivity()).thenReturn(nextActivity);
    return nextActivity;
  }
}
