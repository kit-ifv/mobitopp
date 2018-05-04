package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.routing.DefaultPath;
import edu.kit.ifv.mobitopp.routing.Link;

import java.util.ArrayList;

public class NoRouteChoice 
	implements ZoneBasedRouteChoice {


	public Path selectRoute(
					Time date,
					int sourceZoneId,
					int targetZoneId
	) {

		return DefaultPath.makePath(new ArrayList<Link>());
	}


} 
