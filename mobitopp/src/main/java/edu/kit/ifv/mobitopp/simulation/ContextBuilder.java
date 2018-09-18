package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.ParserBuilder;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.network.NetworkSerializer;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.util.StopWatch;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class ContextBuilder {

	private final  StopWatch performanceLogger;
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
	private DynamicParameters modeChoiceParameters;

	public ContextBuilder() {
		super();
		performanceLogger = new StopWatch(LocalDateTime::now);
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
		startLoading();
		validateConfiguration();
		experimentalParameters();
		modeChoiceParameters();
		resultWriter();
		simulationDays();
		publicTransport();
		dataRepository();
		personResults();
		SimulationContext createContext = createContext();
		printPerformance();
		return createContext;
	}

	private void startLoading() {
		performanceLogger.start();
	}

	private void log(String message) {
		performanceLogger.measurePoint(message);
	}
	
	private void printPerformance() {
		System.out.println("Runtimes while loading context:");
		performanceLogger.forEach((m,d) -> System.out.println(m + " " + d));
	}

	private void validateConfiguration() {
		new Validate().now(configuration);
		log("Validate configuration");
	}

	private void experimentalParameters() {
		experimentalParameters = new DynamicParameters(configuration.getExperimental());
		log("Create experimental parameters");
	}
	
	private void modeChoiceParameters() {
		modeChoiceParameters = new DynamicParameters(configuration.getModeChoice());
		log("Create mode choice parameters");
	}

	private void resultWriter() {
		resultWriter = ResultWriter.create(Convert.asFile(configuration.getResultFolder()));
		electricChargingWriter = new ElectricChargingWriter(resultWriter);
		log("Configure result writers");
	}

	private void simulationDays() {
		simulationDays = SimulationDays.containing(configuration.getDays());
		log("Set simulation days");
	}

	private void loadVisumNetwork() {
		System.out.println("Reading VISUM network");
		if (null == network) {
			network = NetworkSerializer.readVisumNetwork(configuration.getVisumFile());
		}
	}

	private void loadRoadNetwork() {
		loadVisumNetwork();
		System.out.println("creating road network");
		if (null == roadNetwork) {
			roadNetwork = new SimpleRoadNetwork(network);
		}
	}
	
	protected Network network() {
		loadRoadNetwork();
		return new Network(network, roadNetwork);
	}

	private void publicTransport() {
		System.out.println("reading PT network");
		publicTransport = configuration.getPublicTransport().loadData(this::network, simulationDays);
		log("Load public transport");
	}

	private void dataRepository() throws IOException {
		System.out.println("Loading data repository");
		int numberOfZones = configuration.getNumberOfZones();
		dataRepository = configuration.getDataSource().forSimulation(this::network,
				numberOfZones, simulationDays, publicTransport, resultWriter, electricChargingWriter);
		log("Load data repository");
	}

	private void personResults() {
		System.out.println("Configuring output");
		personResults = createResults();
		log("Create output");
	}

	protected PersonResults createResults() {
		return new TripfileWriter(resultWriter, dataRepository.impedance());
	}

	private SimulationContext createContext() {
		SimpleSimulationContext context = new SimpleSimulationContext(configuration,
				experimentalParameters, dataRepository, simulationDays, format, resultWriter,
				electricChargingWriter, personResults, modeChoiceParameters);
		log("Create context");
		return context;
	}

}
