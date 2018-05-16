package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.ParserBuilder;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNetworkReader;
import edu.kit.ifv.mobitopp.visum.VisumReader;

public class ContextBuilder {

	private final SimulationParser format;

	private WrittenConfiguration configuration;
	private DynamicParameters experimentalParameters;
	private ResultWriter resultWriter;
	private ElectricChargingWriter electricChargingWriter;
	private SimulationDays simulationDays;
	private VisumNetwork network;
	private SimpleRoadNetwork roadNetwork;
	private PublicTransportData publicTransport;
	private DataRepositoryForSimulation dataRepository;
	private PersonResults personResults;

	public ContextBuilder() {
		super();
		ParserBuilder parser = new ParserBuilder();
		format = parser.forSimulation();
	}

	public SimulationContext buildFrom(File configurationFile) throws IOException {
		WrittenConfiguration configuration = format.parse(configurationFile);
		return buildFrom(configuration);
	}

	public SimulationContext buildFrom(WrittenConfiguration configuration) throws IOException {
		this.configuration = configuration;
		return loadData();
	}

	private SimulationContext loadData() throws IOException {
		validateConfiguration();
		experimentalParameters();
		resultWriter();
		simulationDays();
		visumNetwork();
		roadNetwork();
		publicTransport();
		dataRepository();
		personResults();
		return createContext();
	}

	private void validateConfiguration() {
		new Validate().now(configuration);
	}

	private void experimentalParameters() {
		experimentalParameters = new DynamicParameters(configuration.getExperimental());
	}

	private void resultWriter() {
		resultWriter = ResultWriter.create(Convert.asFile(configuration.getResultFolder()));
		electricChargingWriter = new ElectricChargingWriter(resultWriter);
	}

	private void simulationDays() {
		simulationDays = SimulationDays.containing(configuration.getDays());
	}

	private void visumNetwork() {
		VisumReader reader = new VisumReader();
		VisumNetworkReader networkReader = new VisumNetworkReader(reader);
		File visumNetworkFile = Convert.asFile(configuration.getVisumFile());
		network = networkReader.readNetwork(visumNetworkFile);
	}

	private void roadNetwork() {
		roadNetwork = new SimpleRoadNetwork(network);
	}

	private void publicTransport() {
		publicTransport = configuration.getPublicTransport().loadData(network, simulationDays);
	}

	private void dataRepository() throws IOException {
		int numberOfZones = configuration.getNumberOfZones();
		dataRepository = configuration.getDataSource().forSimulation(network, roadNetwork,
				numberOfZones, simulationDays, publicTransport, resultWriter, electricChargingWriter);
	}

	private void personResults() {
		personResults = createResults();
	}

	protected PersonResults createResults() {
		return new TripfileWriter(resultWriter, dataRepository.impedance());
	}

	private SimulationContext createContext() {
		return new SimpleSimulationContext(configuration, experimentalParameters, network, roadNetwork,
				dataRepository, simulationDays, format, resultWriter, electricChargingWriter,
				personResults);
	}

}
