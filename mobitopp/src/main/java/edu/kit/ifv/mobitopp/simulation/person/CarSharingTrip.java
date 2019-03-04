package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.TripDecorator;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.time.Time;


public class CarSharingTrip extends TripDecorator implements TripIfc {

  public CarSharingTrip(TripIfc trip, SimulationPerson person) {
    super(trip, person);
  }
  
  @Override
  public void startTrip(ImpedanceIfc impedance, Time currentTime) {
    if (person().currentActivity().activityType().isHomeActivity()) {
      allocateCar(currentTime);
    }
  }

  private void allocateCar(Time currentTime) {
    Zone zone = person().currentActivity().zone();
    assert zone.carSharing().isStationBasedCarSharingCarAvailable(person());

    Car car = zone.carSharing().bookStationBasedCar(person());

    person().useCar(car, currentTime);
  }

}
