package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;

public interface CarSharingListener {

	void bookCar(Zone zone, int availableBefore, CarSharingCar car, int availableCars, String nextAvailableCar, String availableCarsAsString);

	void returnCar(CarSharingCar car, Zone zone, int availableBefore, int availableCars, CarSharingCar nextAvailableCar, String availableCarsAsString);

}
