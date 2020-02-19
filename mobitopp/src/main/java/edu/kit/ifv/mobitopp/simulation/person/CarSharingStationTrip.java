package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.time.Time;

public class CarSharingStationTrip extends CarBasedTrip implements Trip {

  public CarSharingStationTrip(TripData trip, SimulationPerson person) {
    super(trip, person, CarSharingCar.class, StandardMode.CARSHARING_STATION);
  }
  
  @Override
  protected boolean hasPreviouslyUsedCar() {
    return !person().currentActivity().activityType().isHomeActivity();
  }

  @Override
  protected Car allocateCar(ImpedanceIfc impedance, Time currentTime) {
    Zone zone = person().currentActivity().zone();
    assert zone
        .carSharing()
        .isStationBasedCarSharingCarAvailable(person()) : "Carsharing is not available."
            + person().getId();
    return zone.carSharing().bookStationBasedCar(person());
  }
  
  @Override
  protected boolean canReturnCar(ActivityIfc nextActivity) {
    return nextActivity.activityType().isHomeActivity();
  }

  @Override
  protected void notifyBeforeReturn(PersonListener listener, FinishedTrip finishedTrip) {
  }

	@Override
	protected Zone getCarReturnZone() {
		return nextActivity().zone();
	}

}
