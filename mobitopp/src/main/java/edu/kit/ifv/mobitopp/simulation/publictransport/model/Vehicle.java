package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.time.Time;

public interface Vehicle extends Boardable {

	int journeyId();

	Time firstDeparture();

	void moveToNextStop();

	Stop currentStop();

	Optional<Connection> nextConnection();
	
	Optional<Time> nextDeparture();

	Optional<Time> nextArrival();

	void notifyPassengers(EventQueue queue, Time currentDate);

}
