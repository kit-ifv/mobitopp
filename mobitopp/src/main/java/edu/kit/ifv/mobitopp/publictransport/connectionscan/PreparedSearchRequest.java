package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;

interface PreparedSearchRequest {

	Time startTime();
	
	boolean departsAfterArrivalAtEnd(Connection connection);

	void updateArrival(Connection connection);

	Optional<PublicTransportRoute> createRoute();

}