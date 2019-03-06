package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.BaseTrip;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.time.Time;

public class PassengerTrip extends BaseTrip implements TripIfc {

  public PassengerTrip(TripData trip, SimulationPerson person) {
    super(trip, person);
  }

  @Override
  public FinishedTrip finish(Time currentDate, PersonResults results) {
    FinishedTrip finishedTrip = super.finish(currentDate, results);
    if (person().isCarPassenger()) {
      String carId = String.valueOf(person().whichCar().id());
      person().leaveCar();
      return new FinishedPassengerTrip(finishedTrip, carId);
    }
    return new FinishedPassengerTrip(finishedTrip, "-1");
  }

}
