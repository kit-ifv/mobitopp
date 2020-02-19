package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;

public interface TripFactory {

  Trip createTrip(
      SimulationPerson person, ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity,
      ActivityIfc nextActivity, double randomNumber);

  int nextTripId();

  TripData createTripData(
      ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity, ActivityIfc nextActivity);

}
