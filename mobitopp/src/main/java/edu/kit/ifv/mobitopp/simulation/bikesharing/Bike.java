package edu.kit.ifv.mobitopp.simulation.bikesharing;

import edu.kit.ifv.mobitopp.data.Zone;

public interface Bike {

	void returnBike(Zone zone);

	int getId();

}
