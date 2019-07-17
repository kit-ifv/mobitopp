package edu.kit.ifv.mobitopp.result;

import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;

public interface TripConverter {

  String convert(
      Person person, FinishedTrip finishedTrip, ActivityIfc previousActivity, ActivityIfc activity,
      Location location_from, Location location_to);

}
