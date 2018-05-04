package edu.kit.ifv.mobitopp.simulation.activityschedule;

import edu.kit.ifv.mobitopp.time.Time;

public interface OccupationIfc
{ 

	int getOid();

  Time startDate();

  Time calculatePlannedEndDate();

}
