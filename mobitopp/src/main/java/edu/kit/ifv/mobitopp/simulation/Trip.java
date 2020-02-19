package edu.kit.ifv.mobitopp.simulation;

import java.util.Optional;

import edu.kit.ifv.mobitopp.simulation.activityschedule.OccupationIfc;
import edu.kit.ifv.mobitopp.simulation.intermodal.Leg;
import edu.kit.ifv.mobitopp.time.Time;

public interface Trip extends Leg, TripData, OccupationIfc {

  Optional<Time> timeOfNextChange();

}
