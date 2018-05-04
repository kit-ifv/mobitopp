package edu.kit.ifv.mobitopp.populationsynthesis;



import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.PatternActivityWeek;

public interface PersonForSetup
	extends Person 
{
	PatternActivityWeek getPatternActivityWeek();

	void setFixedDestination(ActivityType activityType, Zone zone, Location location);

	void setFixedDestination(FixedDestination fixedDestination);
}
