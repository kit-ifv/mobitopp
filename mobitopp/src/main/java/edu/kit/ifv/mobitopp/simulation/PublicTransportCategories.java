package edu.kit.ifv.mobitopp.simulation;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.result.Category;

public class PublicTransportCategories {

	public final Category vehicle = vehicle();
	public final Category passenger = passenger();
	public final Category routeLeg = routeLeg();
	public final Category stop = stop();
	public final Category searchNewTrip = searchNewTrip();
	public final Category vehicleCrowded = vehicleCrowded();

	private Category vehicle() {
		List<String> header = new ArrayList<>();
		header.add("event");
		header.add("journeyId");
		header.add("stopId");
		header.add("stopName");
		header.add("tripBeginDay");
		header.add("tripBeginTime");
		header.add("passengerCount()");
		return new Category("demandsimulationVehiclePublicTransport", header);
	}

	private Category passenger() {
		List<String> header = new ArrayList<>();
		header.add("event");
		header.add("personOid");
		header.add("journeyId");
		header.add("stopId");
		header.add("stopName");
		header.add("tripDay");
		header.add("tripTime");
		header.add("departure");
		return new Category("demandsimulationPassengerPublicTransport", header);
	}

	private Category routeLeg() {
		List<String> header = new ArrayList<>();
		header.add("personOid");
		header.add("journeyId");
		header.add("tripDay");
		header.add("tripTime");
		header.add("departure");
		header.add("connections");
		return new Category("demandsimulationPassengerPublicTransportLeg", header);
	}

	private Category stop() {
		List<String> header = new ArrayList<>();
		header.add("stopId");
		header.add("stopName");
		header.add("tripDay");
		header.add("tripTime");
		header.add("persons");
		return new Category("demandsimulationStopPublicTransport", header);
	}

	private Category searchNewTrip() {
		List<String> header = new ArrayList<>();
		header.add("personId");
		header.add("stopId");
		header.add("stopName");
		header.add("tripDay");
		header.add("tripTime");
		header.add("journeyId");
		return new Category("demandsimulationSearchNewPublicTransportTrip", header);
	}

	private Category vehicleCrowded() {
		List<String> header = new ArrayList<>();
		header.add("departure");
		header.add("stopId");
		header.add("stopName");
		header.add("journeyId");
		header.add("passengerCount");
		header.add("capacity");
		return new Category("demandsimulationVehicleCrowdedPublicTransport", header);
	}

}
