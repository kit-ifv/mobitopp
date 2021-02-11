package edu.kit.ifv.mobitopp.simulation.publictransport.vehicle;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Map;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicle;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.Vehicles;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimulatedVehicles implements Vehicles {

	private final Map<Journey, Vehicle> journeyToVehicle;
	private final VehicleQueue queue;

	SimulatedVehicles(Map<Journey, Vehicle> journeyToVehicle, VehicleQueue queue) {
		super();
		this.journeyToVehicle = journeyToVehicle;
		this.queue = queue;
	}
	
	@Override
	public Vehicle vehicleServing(Journey journey) {
		if (journeyToVehicle.containsKey(journey)) {
			return journeyToVehicle.get(journey);
		}
		throw warn(new IllegalArgumentException("No vehicle found for journey: " + journey), log);
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
