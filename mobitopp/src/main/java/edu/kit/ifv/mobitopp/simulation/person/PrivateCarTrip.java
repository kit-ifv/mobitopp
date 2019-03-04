package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Objects;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.TripDecorator;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.time.Time;

public class PrivateCarTrip extends TripDecorator implements TripIfc {

  public PrivateCarTrip(TripIfc trip, SimulationPerson person) {
    super(trip, person);
  }

  @Override
  public void startTrip(ImpedanceIfc impedance, Time currentTime) {
    Objects.requireNonNull(impedance);
    Objects.requireNonNull(currentTime);
    if (person().currentActivity().activityType().isHomeActivity()) {
      useCarOfHousehold(impedance, currentTime);
    } else {
      useParkedCar();
    }
  }

  private void useParkedCar() {
    assert person().hasParkedCar();
    assert !person().isCarDriver();
    person().takeCarFromParking();
  }

  private void useCarOfHousehold(ImpedanceIfc impedance, Time currentTime) {
    int origin = origin().zone().getOid();
    int destination = destination().zone().getOid();
    float distance = impedance.getDistance(origin, destination);
    float distanceKm = distance/1000.0f;
    PrivateCar car = person().household().takeAvailableCar(person(), distanceKm);
    person().useCar(car, currentTime);
  }

}
