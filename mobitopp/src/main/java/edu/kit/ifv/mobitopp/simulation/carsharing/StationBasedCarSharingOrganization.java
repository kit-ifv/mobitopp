package edu.kit.ifv.mobitopp.simulation.carsharing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.Zone;

public class StationBasedCarSharingOrganization extends BaseCarSharingOrganization 
	implements CarSharingOrganization 
	, Serializable
{

	private static final long serialVersionUID = 1L;

	protected List<StationBasedCarSharingCar> ownedCars = new ArrayList<>();

	protected Map<Zone, List<CarSharingCar>> availableCars = new HashMap<Zone, List<CarSharingCar>>();

	transient protected Map<Zone, List<CarSharingCar>> currentlyUsedCars = new HashMap<Zone, List<CarSharingCar>>();

	protected List<CarSharingStation> stations = new ArrayList<CarSharingStation>();

	public StationBasedCarSharingOrganization(String name) {
		super(name);
	}

	public void ownCar(StationBasedCarSharingCar car, Zone zone) {
		ownedCars.add(car);

		if (!availableCars.containsKey(zone)) {
			availableCars.put(zone, new ArrayList<CarSharingCar>());
		}
		availableCars.get(zone).add(car);
	}


	public boolean isCarAvailable(Zone zone) {

		if (!availableCars.containsKey(zone)) { return false; }

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


		int available_before = availableCars(zone);

		CarSharingCar car = availableCars.get(zone).remove(0);

		if (currentlyUsedCars == null) {	
			this.currentlyUsedCars =  new HashMap<Zone, List<CarSharingCar>>();
		}

		if (!currentlyUsedCars.containsKey(zone)) {
			currentlyUsedCars.put(zone, new ArrayList<CarSharingCar>());
		}
		currentlyUsedCars.get(zone).add(car);

		int availableCars = availableCars(zone);
		String nextAvailableCar = isCarAvailable(zone) ? 
				nextAvailableCar(zone).id() + "," + nextAvailableCar(zone) : "";
				String availableCarsAsString = availableCarsAsString(zone);
		bookCar(zone, available_before, car, availableCars, nextAvailableCar, availableCarsAsString);

		return car;
	}

	public void returnCar(CarSharingCar car, Zone zone) {
		assert currentlyUsedCars.containsKey(zone);
		assert !currentlyUsedCars.get(zone).isEmpty();
		assert currentlyUsedCars.get(zone).contains(car);
		assert availableCars.containsKey(zone);
		assert !availableCars.get(zone).contains(car);

		int available_before = availableCars(zone);

		currentlyUsedCars.get(zone).remove(car);
		availableCars.get(zone).add(car);

		int availableCars = availableCars(zone);
		CarSharingCar nextAvailableCar = nextAvailableCar(zone);
		String availableCarsAsString = availableCarsAsString(zone);
		returnCar(car, zone, available_before, availableCars, nextAvailableCar,
				availableCarsAsString);
	}
	
	public void clearCars(Zone zone) {

		List<CarSharingCar> cars = availableCars.get(zone);

		if (cars == null) { return; }

		ownedCars.removeAll(cars);

		availableCars.put(zone, new ArrayList<CarSharingCar>());
	}


	private String availableCarsAsString(Zone zone) {

		String s = "";

		for (CarSharingCar car : availableCars.get(zone)) {
			s += car.id() + ",";
		}


		return s;
	}


	public void addStation(CarSharingStation station) {
		assert station != null;

		this.stations.add(station);
	}
	
	public Stream<CarSharingStation> stations() {
		return stations.stream();
	}
	
	public Stream<StationBasedCarSharingCar> ownedCars() {
		return ownedCars.stream();
	}
	
}

