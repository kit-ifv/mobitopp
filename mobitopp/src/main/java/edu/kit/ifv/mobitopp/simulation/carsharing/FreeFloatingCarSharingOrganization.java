package edu.kit.ifv.mobitopp.simulation.carsharing;

import edu.kit.ifv.mobitopp.data.Zone;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;


public class FreeFloatingCarSharingOrganization extends BaseCarSharingOrganization 
	implements CarSharingOrganization 
	, Serializable
{
	public final static long serialVersionUID =  -8230694404457066663L;

	public List<CarSharingCar> ownedCars = new ArrayList<CarSharingCar>();

	protected Map<Zone, List<CarSharingCar>> availableCars = new HashMap<Zone, List<CarSharingCar>>();

	protected transient List<CarSharingCar> currentlyUsedCars = new ArrayList<CarSharingCar>();

	public FreeFloatingCarSharingOrganization(String name) {
		super(name);
	}

	public void ownCar(CarSharingCar car, Zone zone) {
		ownedCars.add(car);

		if (!availableCars.containsKey(zone)) {
			availableCars.put(zone, new ArrayList<CarSharingCar>());
		}
		availableCars.get(zone).add(car);
	}

	public void ownCars(Collection<CarSharingCar> cars, Zone zone) {
		ownedCars.addAll(cars);

		if (!availableCars.containsKey(zone)) {
			availableCars.put(zone, new ArrayList<CarSharingCar>());
		}
		availableCars.get(zone).addAll(cars);
	}

	public boolean isCarAvailable(Zone zone) {
		if (!availableCars.containsKey(zone)) {
			return false;
		}

		return !availableCars.get(zone).isEmpty();
	}

	public int availableCars(Zone zone) {
		if (!availableCars.containsKey(zone)) { return 0; }

		return availableCars.get(zone).size();
	}

	public CarSharingCar nextAvailableCar(Zone zone) {
		assert availableCars.containsKey(zone);
		assert !availableCars.get(zone).isEmpty();

		return availableCars.get(zone).get(0);
	}

	public CarSharingCar bookCar(Zone zone) {
		assert availableCars.containsKey(zone);
		assert !availableCars.get(zone).isEmpty();

		if (currentlyUsedCars == null) {	
			this.currentlyUsedCars = new ArrayList<CarSharingCar>();
		}

		assert currentlyUsedCars != null;

		int availableBefore = availableCars(zone);

		CarSharingCar car = availableCars.get(zone).remove(0);

		currentlyUsedCars.add(car);

		int availableCars = availableCars(zone);
		String nextAvailableCar = isCarAvailable(zone)
				? nextAvailableCar(zone).id() + "," + nextAvailableCar(zone) : "";
		String availableCarsAsString = availableCarsAsString(zone);
		bookCar(zone, availableBefore, car, availableCars, nextAvailableCar, availableCarsAsString);
		return car;
	}

	private String availableCarsAsString(Zone zone) {

		String s = "";

		for (CarSharingCar car : availableCars.get(zone)) {
			s += car.id() + ",";
		}

		return s;
	}

	public void returnCar(CarSharingCar car, Zone zone) {

		assert zone.carSharing().isFreeFloatingZone(car);

		if (!availableCars.containsKey(zone)) {
			availableCars.put(zone, new ArrayList<CarSharingCar>());
		}

		assert currentlyUsedCars.contains(car);
		assert availableCars.containsKey(zone);
		assert !availableCars.get(zone).contains(car);

		int available_before = availableCars(zone);

		currentlyUsedCars.remove(car);
		availableCars.get(zone).add(car);

		int availableCars = availableCars(zone);
		CarSharingCar nextAvailableCar = nextAvailableCar(zone);
		String availableCarsAsString = availableCarsAsString(zone);
		returnCar(car, zone, available_before, availableCars, nextAvailableCar, availableCarsAsString);
	}

	public void clearCars(Zone zone) {

		List<CarSharingCar> cars = availableCars.get(zone);

		if (cars == null) { return; }

		ownedCars.removeAll(cars);

		availableCars.put(zone, new ArrayList<CarSharingCar>());
	}

}

