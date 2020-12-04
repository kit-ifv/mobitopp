package edu.kit.ifv.mobitopp.simulation.activityschedule;



import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.time.Time;

public interface ActivityIfc
  extends OccupationIfc
{ 

  boolean isRunning();
  void setRunning(boolean running_);

  byte getActivityNrOfWeek();
 
	ActivityType activityType();

  boolean isModeSet();
  void setMode(Mode mode);  
  Mode mode();

  Time startDate();
  void setStartDate(Time date_);
  
  Time calculatePlannedEndDate();

	int duration();
  void changeDuration(int newDuration);

	int observedTripDuration();

	int getOid();

	boolean isLocationSet();
	
	void setLocation(ZoneAndLocation zoneAndlocation);
	Zone zone();
	Location location();
	ZoneAndLocation zoneAndLocation();
	
	boolean hasFixedDuration();
	boolean hasFixedStart();
	boolean hasFixedEnd();
	
	float durationFlexibility();
	float startFlexibility();
	float endFlexibility();

}
