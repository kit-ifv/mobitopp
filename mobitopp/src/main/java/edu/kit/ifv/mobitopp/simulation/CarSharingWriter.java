package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.result.Category;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;

public class CarSharingWriter implements CarSharingListener {

	private final Results results;
	private final CarSharingCategories categories;

	public CarSharingWriter(Results results) {
		super();
		this.results = results;
		categories = new CarSharingCategories();
	}

	@Override
	public void bookCar(
			Zone zone, int availableBefore, CarSharingCar car, int availableCars, String nextAvailableCar,
			String availableCarsAsString) {
		String message = "booked car in zone " + zone.getId() + " is " + car.id();
		message += "\n car is: " + car;
		message += "\n number of available cars before: " + availableBefore;
		message += "\n number of available cars: " + availableCars;
		message += "\n next available car is: " + nextAvailableCar;
		message += "\n cars: " + availableCarsAsString;
		write(categories.carsharing, message);
	}

	@Override
	public void returnCar(
			CarSharingCar car, Zone zone, int availableBefore, int availableCars,
			CarSharingCar nextAvailableCar, String availableCarsAsString) {
		String message = "returned car in zone " + zone.getId() + " is " + car.id();
		message += "\n car is: " + car;
		message += "\n number of available cars before: " + availableBefore;
		message += "\n number of available cars: " + availableCars;
		message += "\n next available car is: " + nextAvailableCar;
		message += "\n cars: " + availableCarsAsString;
		write(categories.carsharing, message);
	}

	protected void write(Category category, String message) {
		if (null == results) {
			System.out.println("No result writer registered.");
			return;
		}
		results.write(category, message);
	}
}
