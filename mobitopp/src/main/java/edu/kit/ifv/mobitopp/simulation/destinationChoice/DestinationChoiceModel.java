package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface DestinationChoiceModel {

  Zone selectDestination(
      Person person, Optional<Mode> tourMode, ActivityIfc previousActivity,
      ActivityIfc nextActivity, double randomNumber);

  default boolean isTourBased() {
    return false;
  }

}
