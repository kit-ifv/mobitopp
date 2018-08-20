package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public interface Activity {

	Time plannedStart();

	RelativeTime plannedDuration();

	ActivityType activityType();

	RelativeTime expectedTripDuration();

}
