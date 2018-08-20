package edu.kit.ifv.mobitopp.simulation.carsharing;

import edu.kit.ifv.mobitopp.data.Zone;

public class FreeFloatingCar {

	public final Zone startZone;
	public final CarSharingCar car;

	public FreeFloatingCar(Zone startZone, CarSharingCar car) {
		super();
		this.startZone = startZone;
		this.car = car;
	}

}
