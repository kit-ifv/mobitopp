package edu.kit.ifv.mobitopp.simulation.publictransport;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connections;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StationFinder;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Stations;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.StopPoints;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;
import edu.kit.ifv.mobitopp.simulation.publictransport.vehicle.VehiclesConverter;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public abstract class BasePublicTransportConverter implements PublicTransportConverter {

	static final String walkingCode = "F";
	static final String walkingIndividual = "Fuss";
	private final List<Time> simulationDates;

	protected BasePublicTransportConverter(List<Time> simulationDates) {
		this.simulationDates = ensureAtBeginning(simulationDates);
	}

	protected static List<VisumTransportSystem> defaultWalkingSystems(VisumRoadNetwork network) {
		List<VisumTransportSystem> walkingSystems = new ArrayList<>();
		if (network.containsTransportSystem(walkingCode)) {
			VisumTransportSystem publicWalking = network.getTransportSystem(walkingCode);
			walkingSystems.add(publicWalking);
		}
		if (network.containsTransportSystem(walkingIndividual)) {
			VisumTransportSystem individualWalking = network.getTransportSystem(walkingIndividual);
			walkingSystems.add(individualWalking);
		}
		return walkingSystems;
	}

	protected List<Time> ensureAtBeginning(List<Time> simulationDates) {
		return simulationDates.stream().map(Time::startOfDay).collect(toList());
	}

	@Override
	public List<Time> simulationDates() {
		return simulationDates;
	}
	
	@Override
	public PublicTransportTimetable convert() {
		System.out.println("Start converting visum network to mobiTopp");
		Stations stations = convertStations();
		createFactory(stations);
		StopPoints stopPoints = convertStopPoints(stations);
		StationFinder finder = createStationFinder(stations);
		ModifiableJourneys journeys = convertJourneys(stopPoints);
		Connections connections = convertConnections(stopPoints, journeys);
		Vehicles vehicles = convertVehicles(journeys);
		System.out.println("Converting visum network to mobiTopp finished");
		return new PublicTransportTimetable(connections, stopPoints, journeys, finder, stations, vehicles);
	}

	public Vehicles convertVehicles(ModifiableJourneys journeys) {
		return vehicleConverter().convert(journeys);
	}

	protected VehiclesConverter vehicleConverter() {
		return new VehiclesConverter();
	}

	protected abstract StopPoints convertStopPoints(Stations stations);
	
	protected abstract ModifiableJourneys convertJourneys(StopPoints stopPoints);

	protected abstract Connections convertConnections(StopPoints stopPoints, ModifiableJourneys journeys);

	protected abstract Stations convertStations();

	protected abstract StationFinder createStationFinder(Stations stations);
	
	protected abstract void createFactory(Stations stations);

}