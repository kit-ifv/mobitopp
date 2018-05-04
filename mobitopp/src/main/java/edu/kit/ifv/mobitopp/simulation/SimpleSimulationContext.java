package edu.kit.ifv.mobitopp.simulation;

import java.io.PrintStream;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class SimpleSimulationContext implements SimulationContext {

	private final WrittenConfiguration configuration;
	private final VisumNetwork network;
	private final SimpleRoadNetwork roadNetwork;
	private final DataRepositoryForSimulation dataRepository;
	private final SimulationParser format;
	private final ResultWriter resultWriter;
	private final SimulationDays simulationDays;
	private final ElectricChargingWriter electricChargingWriter;
	private final PersonResults personResults;
	private DynamicParameters experimentalParameters;

	public SimpleSimulationContext(
			WrittenConfiguration configuration, DynamicParameters experimentalParameters,
			VisumNetwork network, SimpleRoadNetwork roadNetwork,
			DataRepositoryForSimulation dataRepository, SimulationDays simulationDays,
			SimulationParser format, ResultWriter resultWriter,
			ElectricChargingWriter electricChargingWriter, PersonResults personResults) {
		this.configuration = configuration;
		this.experimentalParameters = experimentalParameters;
		this.network = network;
		this.roadNetwork = roadNetwork;
		this.dataRepository = dataRepository;
		this.simulationDays = simulationDays;
		this.format = format;
		this.resultWriter = resultWriter;
		this.electricChargingWriter = electricChargingWriter;
		this.personResults = personResults;
	}

	@Override
	public DataRepositoryForSimulation dataRepository() {
		return dataRepository;
	}

	@Override
	public PersonLoader personLoader() {
		return dataRepository.personLoader();
	}

	@Override
	public ZoneRepository zoneRepository() {
		return dataRepository.zoneRepository();
	}

	@Override
	public ImpedanceIfc impedance() {
		return dataRepository.impedance();
	}

	@Override
	public VehicleBehaviour vehicleBehaviour() {
		return dataRepository.vehicleBehaviour();
	}

	@Override
	public VisumNetwork network() {
		return network;
	}

	@Override
	public SimpleRoadNetwork roadNetwork() {
		return roadNetwork;
	}

	@Override
	public SimulationDays simulationDays() {
		return simulationDays;
	}

	@Override
	public ResultWriter results() {
		return resultWriter;
	}

	@Override
	public PersonResults personResults() {
		return personResults;
	}

	@Override
	public WrittenConfiguration configuration() {
		return configuration;
	}

	@Override
	public DynamicParameters experimentalParameters() {
		return experimentalParameters;
	}

	@Override
	public void beforeSimulation() {
		printStartupInformationOn(System.out);
		electricChargingWriter.clear();
	}

	private void printStartupInformationOn(PrintStream out) {
		out.println("Configuration:");
		out.print(format.serialise(configuration));
	}

	@Override
	public void afterSimulation() {
		electricChargingWriter.print();
		resultWriter.close();
	}

	@Override
	public long seed() {
		return configuration.getSeed();
	}

	@Override
	public float fractionOfPopulation() {
		return configuration.getFractionOfPopulation();
	}
}
