package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.time.Time;

public interface Vehicle extends Boardable {

	int journeyId();

	Time firstDeparture();

	void moveToNextStop(Time current);

	Stop currentStop();

	Optional<ConnectionId> nextConnection();
	
	Optional<Time> nextDeparture();

	Optional<Time> nextArrival();

	void notifyPassengers(EventQueue queue, Time currentDate);


}
