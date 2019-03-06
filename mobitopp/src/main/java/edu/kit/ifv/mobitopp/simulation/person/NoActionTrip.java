package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.BaseTrip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.TripIfc;


public class NoActionTrip extends BaseTrip implements TripIfc {

  public NoActionTrip(TripData data, SimulationPerson person) {
    super(data, person);
  }

}
