package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public interface TripData {

  ActivityIfc previousActivity();

  ActivityIfc nextActivity();

  Mode mode();

  Time startDate();

  int plannedDuration();

  int getOid();
  
  int getLegId();

  Time calculatePlannedEndDate();

  ZoneAndLocation origin();

  ZoneAndLocation destination();

}