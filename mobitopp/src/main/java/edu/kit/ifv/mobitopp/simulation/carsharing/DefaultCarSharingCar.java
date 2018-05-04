package edu.kit.ifv.mobitopp.simulation.carsharing;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.car.CarDecorator;

public class DefaultCarSharingCar extends CarDecorator implements CarSharingCar, Car, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final CarSharingOrganization owner;

	public DefaultCarSharingCar(
		Car car,
		CarSharingOrganization owner
	) {
		super(car);

		this.owner = owner;
	}

	public CarSharingOrganization owner() {
		return this.owner;
	}

	public void returnCar(Zone zone) {
		this.owner.returnCar(this, zone);
	}

	public String forLogging() {
		StringBuffer buffer = new StringBuffer();

		String personal = "2"; // 2: CarSharingCar
	
		buffer.append("C; ");
		buffer.append(owner.name() + "; ");
		buffer.append("; ");
		buffer.append(personal + "; ");
		buffer.append(car.forLogging());

		return buffer.toString();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
	
		buffer.append("(DefaultCarSharingCar: ");
		buffer.append(car.id() + ",");
		buffer.append(car.carSegment() + ",");
		buffer.append(car.getType().substring(0,1).toUpperCase() + ",");
		buffer.append(car.maxRange() + ",");
		buffer.append(car.currentMileage() + ",");
		buffer.append(car.currentFuelLevel() + ",");
		buffer.append(owner.name() + ")");

		return buffer.toString();
	}
}
