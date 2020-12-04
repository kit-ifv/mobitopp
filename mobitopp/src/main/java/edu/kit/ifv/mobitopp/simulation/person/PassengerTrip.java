package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.BaseTrip;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.time.Time;

public class PassengerTrip extends BaseTrip implements Trip {

  public PassengerTrip(TripData trip, SimulationPerson person) {
    super(trip, person);
  }

  @Override
  public FinishedTrip finish(Time currentDate, PersonListener listener) {
    FinishedTrip finishedTrip = super.finish(currentDate, listener);
    if (person().isCarPassenger()) {
      String carId = String.valueOf(person().whichCar().id());
      person().leaveCar();
      return new FinishedPassengerTrip(finishedTrip, carId);
    }
    return new FinishedPassengerTrip(finishedTrip, "-1");
  }

}
