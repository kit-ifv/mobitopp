package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.PersonResults;
import edu.kit.ifv.mobitopp.simulation.TripDecorator;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.time.Time;

public class CarSharingStationTrip extends TripDecorator implements TripIfc {

  public CarSharingStationTrip(TripIfc trip, SimulationPerson person) {
    super(trip, person);
  }
  
  @Override
  public void prepareTrip(ImpedanceIfc impedance, Time currentTime) {
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
  
  @Override
  public FinishedTrip finish(Time currentDate, PersonResults results) {
    FinishedTrip finishedTrip = super.finish(currentDate, results);
    Zone zone = nextActivity().zone();
    if (nextActivity().activityType().isHomeActivity()) {
      Car car = person().releaseCar(currentDate);
      car.returnCar(zone);
    } else {
      Location location = nextActivity().location();
      Car car = person().parkCar(zone, location, currentDate);
      assert car instanceof CarSharingCar;
    }
    return finishedTrip;
  }

}
