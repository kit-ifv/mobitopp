package edu.kit.ifv.mobitopp.simulation;

import java.time.Duration;
import java.time.LocalDateTime;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Simulation {

	private final SimulationContext context;

	public Simulation(SimulationContext context) {
		super();
		this.context = context;
	}

	protected SimulationContext context() {
		return context;
	}

	protected ZoneRepository zoneRepository() {
		return context().zoneRepository();
	}

	protected ImpedanceIfc impedance() {
		return context().impedance();
	}

	public void simulate() {
		context().beforeSimulation();
		doSimulate();
		context().afterSimulation();
	}

	private void doSimulate() {
		log.info("Initializing sub-systems...");
		DemandSimulator simulator = simulator();

		log.info("ready for simulation");

		LocalDateTime start = LocalDateTime.now();
		simulator.startSimulation();
		LocalDateTime end = LocalDateTime.now();
		Duration runtime = Duration.between(start, end);
		log.info("Start: " + start + " End: " + end + " Runtime: " + runtime);
	}

	abstract protected DemandSimulator simulator();

}
