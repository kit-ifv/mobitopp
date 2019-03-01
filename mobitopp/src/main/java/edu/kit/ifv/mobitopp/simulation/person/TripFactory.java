package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface TripFactory {

  TripIfc createTrip(
      SimulationPerson person, ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity,
      ActivityIfc nextActivity);

}
