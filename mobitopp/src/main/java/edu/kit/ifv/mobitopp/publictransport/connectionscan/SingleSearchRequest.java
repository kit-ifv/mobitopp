package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

class SingleSearchRequest extends BaseSearchRequest {

	private final Stop start;
	private final Stop end;

	private SingleSearchRequest(
			Stop start, Stop end, ArrivalTimes times, UsedConnections usedConnections, UsedJourneys usedJourneys) {
		super(times, usedConnections, usedJourneys);
		this.start = start;
		this.end = end;
	}

	static PreparedSearchRequest from(Stop start, Stop end, Time atTime, int totalNumberOfStopsInNetwork) {
		ArrivalTimes times = SingleStart.create(start, atTime, totalNumberOfStopsInNetwork);
		UsedConnections usedConnections = new DefaultUsedConnections(totalNumberOfStopsInNetwork);
		UsedJourneys usedJourneys = new DefaultUsedJourneys();
		BaseSearchRequest searchRequest = new SingleSearchRequest(start, end, times, usedConnections, usedJourneys);
		times.initialise(searchRequest::initialise);
		return searchRequest;
	}

	@Override
	public boolean departsAfterArrivalAtEnd(Connection connection) {
		return isAfterArrivalAt(connection.departure(), end);
	}

	@Override
	protected List<Connection> collectConnections(UsedConnections usedConnections, Time time)
			throws StopNotReachable {
		return usedConnections.collectConnections(start, end);
	}
}