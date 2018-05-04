package edu.kit.ifv.mobitopp.data.local.configuration;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.PublicTransportData;
import edu.kit.ifv.mobitopp.simulation.PublicTransportResults;
import edu.kit.ifv.mobitopp.simulation.VehicleBehaviour;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportImpedance;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportTimetable;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.PublicTransportLogger;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.TripfileLogger;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;

public class ExistingPublicTransportData implements PublicTransportData {

	private final PublicTransportTimetable timetable;
	private final Capacity capacity;
	private final RouteSearch routeSearch;

	public ExistingPublicTransportData(
			PublicTransportTimetable timetable, Capacity capacity, RouteSearch routeSearch) {
		super();
		this.timetable = timetable;
		this.capacity = capacity;
		this.routeSearch = routeSearch;
	}

	@Override
	public VehicleBehaviour vehicleBehaviour(ResultWriter results) {
		Vehicles vehicles = timetable.vehicles();
		PublicTransportResults ptResults = new PublicTransportResults(results);
		PublicTransportLogger logger = new TripfileLogger(ptResults);
		return capacity.createVehicleBehaviour(routeSearch, logger, vehicles);
	}

	@Override
	public ImpedanceIfc impedance(ImpedanceIfc impedance, ZoneRepository zoneRepository) {
		return new PublicTransportImpedance(routeSearch, timetable, impedance);
	}
}
