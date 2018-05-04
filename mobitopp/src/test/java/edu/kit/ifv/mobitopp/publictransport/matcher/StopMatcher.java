package edu.kit.ifv.mobitopp.publictransport.matcher;

import org.hamcrest.Matcher;

import edu.kit.ifv.mobitopp.publictransport.matcher.stop.NeighbourMatcher;
import edu.kit.ifv.mobitopp.publictransport.matcher.stop.NoNeighbourMatcher;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class StopMatcher {

	public static Matcher<Stop> hasNeighbour(Stop stop) {
		return new NeighbourMatcher(stop);
	}

	public static Matcher<Stop> hasNoNeighbour() {
		return new NoNeighbourMatcher();
	}
}
