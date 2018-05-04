package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.time.Time;

public interface ZoneBasedRouteChoice {


	Path selectRoute(
					Time date,
					int sourceZoneId,
					int targetZoneId
	);


} 
