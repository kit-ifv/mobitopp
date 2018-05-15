package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public interface FinishedTrip {

	int getOid();

	ZoneAndLocation origin();

	ZoneAndLocation destination();

	Mode mode();

	Time startDate();

	Time endDate();

	Time plannedEndDate();

	int plannedDuration();

	ActivityIfc previousActivity();

	ActivityIfc nextActivity();

	Statistic statistic();

}