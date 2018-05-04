package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;

public class LimitedCapacity extends BasicPublicTransportBehaviour {

	public LimitedCapacity(
			RouteSearch routeSearch, PublicTransportLogger logger, Vehicles vehicles) {
		super(routeSearch, logger, vehicles);
	}

	@Override
	protected boolean hasPlaceInVehicle(Vehicles vehicles, PublicTransportLeg leg) {
		return vehicles.vehicleServing(leg.journey()).hasFreePlace();
	}

}
