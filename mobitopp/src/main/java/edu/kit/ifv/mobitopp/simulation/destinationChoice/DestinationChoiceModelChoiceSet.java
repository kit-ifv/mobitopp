package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

import java.util.Optional;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;

public interface DestinationChoiceModelChoiceSet 
{

  public Zone selectDestination(
      Person person, 
      Optional<Mode> tourMode,
      ActivityIfc previousActivity,
			ActivityIfc nextActivity,
			Set<Zone> choiceSet, double randomNumber
	);
  
  default public boolean isTourBased() {
  	return false;
  }

}
