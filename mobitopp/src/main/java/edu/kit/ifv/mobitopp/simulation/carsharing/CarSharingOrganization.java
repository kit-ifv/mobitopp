package edu.kit.ifv.mobitopp.simulation.carsharing;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.CarSharingListener;

public interface CarSharingOrganization 
{

	public String name();

	public boolean isCarAvailable(Zone zone);

	public CarSharingCar nextAvailableCar(Zone zone);

	public CarSharingCar bookCar(Zone zone);

	public void returnCar(CarSharingCar car, Zone zone);
	
	public void returnCarToOrigin(CarSharingCar car);

	public void clearCars(Zone zone);

	void register(CarSharingListener listener);

}
