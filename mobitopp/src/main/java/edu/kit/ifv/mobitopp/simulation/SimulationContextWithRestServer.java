package edu.kit.ifv.mobitopp.simulation;

import java.io.IOException;
import java.io.PrintStream;

import edu.kit.ifv.mobitopp.communication.JsonResource;
import edu.kit.ifv.mobitopp.communication.RestServerResourceRegistry;
import edu.kit.ifv.mobitopp.communication.SimulationProgressData;
import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.result.Results;

public class SimulationContextWithRestServer implements SimulationContext {

	private final SimulationContext context;
	private final RestServerResourceRegistry restServer;

	public SimulationContextWithRestServer(SimulationContext context) {
		this.context = context;
		this.restServer = RestServerResourceRegistry.create(context.configuration().getPort());
	}

	@Override
	public DataRepositoryForSimulation dataRepository() {
		return context.dataRepository();
	}

	@Override
	public PersonLoader personLoader() {
		return context.personLoader();
	}

	@Override
	public ZoneRepository zoneRepository() {
		return context.zoneRepository();
	}

	@Override
	public ImpedanceIfc impedance() {
		return context.impedance();
	}

	@Override
	public VehicleBehaviour vehicleBehaviour() {
		return context.vehicleBehaviour();
	}

	@Override
	public SimulationDays simulationDays() {
		return context.simulationDays();
	}

	@Override
	public Results results() {
		return context.results();
	}

	@Override
	public PersonResults personResults() {
		return context.personResults();
	}

	@Override
	public DynamicParameters destinationChoiceParameters() {
		return context.destinationChoiceParameters();
	}

	@Override
	public DynamicParameters modeChoiceParameters() {
		return context.modeChoiceParameters();
	}

	@Override
	public WrittenConfiguration configuration() {
		return context.configuration();
	}

	@Override
	public DynamicParameters experimentalParameters() {
		return context.experimentalParameters();
	}

	/**
	 * This method should be called before the simulation
	 * for set up:
	 * i.e. starting the {@link RestServerResourceRegistry rest server},
	 * and preparing {@link ElectricChargingWriter electric charging results}.
	 */
	@Override
	public void beforeSimulation() {
		context.beforeSimulation();
		
		if (restServer != null) {
			restServer.start();
		}
	}

	/**
	 * This method should be called after the simulation
	 * for cleaning up:
	 * i.e. stopping the {@link RestServerResourceRegistry rest server},
	 * and finishing off the simulation results.
	 */
	@Override
	public void afterSimulation() {
		if (restServer != null) {
			restServer.stop();
		}

		context.afterSimulation();
	}

	@Override
	public long seed() {
		return context.seed();
	}

	@Override
	public float fractionOfPopulation() {
		return context.fractionOfPopulation();
	}

	public RestServerResourceRegistry restServer() {
		return restServer;
	}
	
	public void registerProgressResource(DemandSimulatorPassenger simulator) {
		if (this.restServer() != null) {
			SimulationProgressData data = new SimulationProgressData(0, simulator.simulationEnd().toSeconds());
		
			Hook update = time -> data.setSimulation_second(time.toSeconds());
			simulator.addBeforeTimeSliceHook(update);
		
			JsonResource resource = new JsonResource(data, "/rest/simulation/progress");
			this.restServer().registerResource(resource);
		} else  {
			System.err.println("Server is null");
		}
		
	}

}
