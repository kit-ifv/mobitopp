package edu.kit.ifv.mobitopp.simulation.person;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionId;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public interface PublicTransportLeg {

	int journeyId();

	Stop start();

	Stop end();

	Journey journey();

	Time arrival();

	Time departure();

	List<Connection> connections();

	ConnectionId firstConnection();

}