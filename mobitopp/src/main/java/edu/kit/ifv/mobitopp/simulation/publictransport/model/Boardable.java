package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public interface Boardable {

	void board(Passenger person, Stop exitStop);

	void getOff(Passenger person);

	int passengerCount();

	boolean hasFreePlace();

}