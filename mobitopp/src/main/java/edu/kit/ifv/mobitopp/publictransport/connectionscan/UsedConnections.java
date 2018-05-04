package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

interface UsedConnections {

	void update(Stop stop, Connection connection);

	List<Connection> collectConnections(Stop fromStart, Stop toEnd) throws StopNotReachable;

	List<Connection> collectConnections(StopPaths fromStarts, Stop toEnd, Time atTime)
			throws StopNotReachable;

}