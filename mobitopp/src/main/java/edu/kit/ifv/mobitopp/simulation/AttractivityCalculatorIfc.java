package edu.kit.ifv.mobitopp.simulation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface AttractivityCalculatorIfc {


	public Map<Zone, Float> calculateAttractivities(
		Person person,
		ActivityIfc nextActivity,
  	Zone currentZone,
  	Collection<Zone> possibleTargetZones,
  	ActivityType activityType,
  	Set<Mode> choiceSetForModes
	);

}
