package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.BaseTrip;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripData;


public class NoActionTrip extends BaseTrip implements Trip {

  public NoActionTrip(TripData data, SimulationPerson person) {
    super(data, person);
  }

}
