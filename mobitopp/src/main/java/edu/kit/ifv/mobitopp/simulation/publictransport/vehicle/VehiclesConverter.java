package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.publictransport.model.FootJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.ModifiableJourneys;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;
import edu.kit.ifv.mobitopp.time.Time;

public class VehiclesConverter {

	private final Vehicle footVehicle;
	private final VehicleFactory factory;

	public VehiclesConverter(VehicleFactory factory) {
		super();
		footVehicle = new FootVehicle();
		this.factory = factory;
	}

	public VehiclesConverter() {
		this(new DefaultVehicleFactory());
	}

	public Vehicles convert(ModifiableJourneys journeys) {
		Map<Journey, Vehicle> journeyToVehicle = journeys.stream().collect(
				toMap(Function.identity(), factory::createFrom));
		VehicleQueue queue = generateInitialEvents(journeyToVehicle);
		HashMap<Journey, Vehicle> allJourneys = addFootjourney(journeyToVehicle);
		return new SimulatedVehicles(allJourneys, queue);
	}

	private HashMap<Journey, Vehicle> addFootjourney(Map<Journey, Vehicle> journeyToVehicle) {
		HashMap<Journey, Vehicle> vehicles = new HashMap<>(journeyToVehicle);
		vehicles.put(FootJourney.footJourney, footVehicle);
		return vehicles;
	}

	private VehicleQueue generateInitialEvents(Map<Journey, Vehicle> journeyToVehicle) {
		SimulatedVehicleQueue queue = new SimulatedVehicleQueue();
		for (Vehicle vehicle : journeyToVehicle.values()) {
			Time firstDeparture = vehicle.firstDeparture();
			VehicleEvent event = new VehicleEvent(firstDeparture, vehicle);
			queue.add(event);
		}
		return queue;
	}
}
