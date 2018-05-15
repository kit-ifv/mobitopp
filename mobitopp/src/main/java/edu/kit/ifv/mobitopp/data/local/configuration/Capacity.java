package edu.kit.ifv.mobitopp.data.local.configuration;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.simulation.VehicleBehaviour;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.LimitedCapacity;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.PublicTransportResults;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.UnlimitedCapacity;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;

public enum Capacity {

	limited {

		@Override
		public VehicleBehaviour createVehicleBehaviour(
				RouteSearch routeSearch, PublicTransportResults results, Vehicles vehicles) {
			return new LimitedCapacity(routeSearch, results, vehicles);
		}
	},
	unlimited {

		@Override
		public VehicleBehaviour createVehicleBehaviour(
				RouteSearch routeSearch, PublicTransportResults results, Vehicles vehicles) {
			return new UnlimitedCapacity(routeSearch, results, vehicles);
		}
	};

	public abstract VehicleBehaviour createVehicleBehaviour(
			RouteSearch routeSearch, PublicTransportResults results, Vehicles vehicles);
}
