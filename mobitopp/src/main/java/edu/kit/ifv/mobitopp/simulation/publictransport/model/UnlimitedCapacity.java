package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;

public class UnlimitedCapacity extends BasicPublicTransportBehaviour {

	public UnlimitedCapacity(
			RouteSearch routeSearch, PublicTransportResults results, Vehicles vehicles) {
		super(routeSearch, results, vehicles);
	}

	@Override
	protected boolean hasPlaceInVehicle(Vehicles vehicles, PublicTransportLeg leg) {
		return true;
	}

}
