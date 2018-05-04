package edu.kit.ifv.mobitopp.simulation.publictransport;

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

public class SimulatedVehicles implements Vehicles {

	static final Vehicle footVehicle = new FootVehicle();
	private final Map<Journey, Vehicle> journeyToVehicle;
	private final VehicleQueue queue;

	private SimulatedVehicles(Map<Journey, Vehicle> journeyToVehicle, VehicleQueue queue) {
		super();
		this.journeyToVehicle = journeyToVehicle;
		this.queue = queue;
	}
	
	SimulatedVehicles(Map<Journey, Vehicle> journeyToVehicle) {
		this(journeyToVehicle, new SimulatedVehicleQueue());
	}

	public static Vehicles from(ModifiableJourneys journeys) {
		Map<Journey, Vehicle> journeyToVehicle = journeys
				.stream()
				.collect(toMap(Function.identity(), SimulatedVehicle::from));
		VehicleQueue queue = generateInitialEvents(journeyToVehicle);
		HashMap<Journey, Vehicle> allJourneys = addFootjourney(journeyToVehicle);
		return new SimulatedVehicles(allJourneys, queue);
	}

	private static HashMap<Journey, Vehicle> addFootjourney(Map<Journey, Vehicle> journeyToVehicle) {
		HashMap<Journey, Vehicle> vehicles = new HashMap<>(journeyToVehicle);
		vehicles.put(FootJourney.footJourney, footVehicle);
		return vehicles;
	}

	private static VehicleQueue generateInitialEvents(
			Map<Journey, Vehicle> journeyToVehicle) {
		SimulatedVehicleQueue queue = new SimulatedVehicleQueue();
		for (Vehicle vehicle : journeyToVehicle.values()) {
			Time firstDeparture = vehicle.firstDeparture();
			VehicleEvent event = new VehicleEvent(firstDeparture, vehicle);
			queue.add(event);
		}
		return queue;
	}

	@Override
	public Vehicle vehicleServing(Journey journey) {
		if (journeyToVehicle.containsKey(journey)) {
			return journeyToVehicle.get(journey);
		}
		throw new IllegalArgumentException("No vehicle found for journey: " + journey);
	}

	@Override
	public boolean hasNextUntil(Time time) {
		return queue.hasNextUntil(time);
	}

	@Override
	public Vehicle next() {
		return queue.next();
	}
	
	@Override
	public void add(Vehicle vehicle, Time nextProcessingTime) {
		queue.add(nextProcessingTime, vehicle);
	}

}
