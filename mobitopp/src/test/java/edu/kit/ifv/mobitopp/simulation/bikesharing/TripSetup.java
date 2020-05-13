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
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
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
	final Zone origin;
	final Location start;
	final Zone destination;
	final Location end;
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
		origin = ExampleZones.create().someZone();
		start = origin.centroidLocation();
		destination = ExampleZones.create().otherZone();
		end = destination.centroidLocation();
	}

	public static TripSetup create() {
		TripSetup tripSetup = new TripSetup();
		when(tripSetup.tripData.origin())
				.thenReturn(new ZoneAndLocation(tripSetup.origin, tripSetup.start));
		when(tripSetup.tripData.destination())
				.thenReturn(new ZoneAndLocation(tripSetup.destination, tripSetup.end));
		return tripSetup;
	}

	public void configureCurrentActivity(ActivityType type) {
		ActivityIfc activity = createActivity(type);
		when(person.currentActivity()).thenReturn(activity);
	}

	public ActivityIfc createActivity(ActivityType type) {
		ActivityIfc activity = mock(ActivityIfc.class);
		lenient().when(activity.activityType()).thenReturn(type);
		lenient().when(activity.location()).thenReturn(origin.centroidLocation());
		return activity;
	}

	public ActivityIfc configureNextActivity(ActivityType type) {
		ActivityIfc nextActivity = createActivity(type);
		when(tripData.nextActivity()).thenReturn(nextActivity);
		return nextActivity;
	}
}
