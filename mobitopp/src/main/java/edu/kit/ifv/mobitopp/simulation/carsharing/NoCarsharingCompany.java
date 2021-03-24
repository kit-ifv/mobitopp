package edu.kit.ifv.mobitopp.simulation.carsharing;


import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.CarSharingListener;

public class NoCarsharingCompany 
	implements CarSharingOrganization
{


	public String name() {
		return "NoCarSharingCompany";
	}

	public boolean isCarAvailable(Zone zone) {
		return false;
	}

	public CarSharingCar nextAvailableCar(Zone zone) {
		throw new AssertionError();
	}

	public CarSharingCar bookCar(Zone zone) {
		throw new AssertionError();
	}

	public void returnCar(CarSharingCar car, Zone zone) {
		throw new AssertionError();
	}

	public void clearCars(Zone zone) {
		throw new AssertionError();
	}

	@Override
	public void register(CarSharingListener listener) {
	}

	@Override
	public void returnCarToOrigin(CarSharingCar car) {
		throw new AssertionError();
	}

}

