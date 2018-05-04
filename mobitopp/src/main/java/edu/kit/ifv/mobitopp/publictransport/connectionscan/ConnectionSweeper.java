package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.Optional;

import edu.kit.ifv.mobitopp.time.Time;

interface ConnectionSweeper {

	boolean areDepartedBefore(Time time);

	Optional<PublicTransportRoute> sweep(PreparedSearchRequest searchRequest);
	
}