package edu.kit.ifv.mobitopp.simulation.carsharing;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.simulation.Car;

public class StationBasedCarSharingCar extends DefaultCarSharingCar
		implements CarSharingCar, Car, Serializable {

	private static final long serialVersionUID = 1L;

	public final CarSharingStation station;

	public StationBasedCarSharingCar(
		Car car,
		CarSharingOrganization owner,
		CarSharingStation station
	) {
		super(car, owner);

		this.station = station;
	}

}
