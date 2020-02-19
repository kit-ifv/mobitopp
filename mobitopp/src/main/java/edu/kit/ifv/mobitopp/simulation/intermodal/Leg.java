package edu.kit.ifv.mobitopp.simulation.intermodal;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.time.Time;

public interface Leg {

  /**
   * Prepare this trip before it gets started. In this method the car is typically taken from the
   * owner.
   * 
   * @param impedance
   *          impedance to access travel time, distance and other network attributes
   * @param currentTime
   *          current simulation time
   */
  void prepareTrip(ImpedanceIfc impedance, Time currentTime);

  /**
   * Finish the execution of the trip. In this method the car is typically returned to its owner or
   * parked.
   * 
   * @param currentDate
   *          current simulation time
   * @param listener
   *          listener to notify output writers and others about changes
   * @return a finished trip containing all information about the executed trip
   */
  FinishedTrip finish(Time currentDate, PersonListener listener);

	int plannedDuration();

}
