package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
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

	private final StopWatch performanceLogger;
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
		resultWriter();
		simulationDays();
		log("Load configuration");
		System.out.println("Reading VISUM network");
		visumNetwork();
		log("Load visum network");
		System.out.println("creating road network");
		roadNetwork();
		log("Load road network");
		System.out.println("reading PT network");
		publicTransport();
		log("Load PT network");
		System.out.println("Creating data repository");
		dataRepository();
		log("Create data repository");
		System.out.println("Preparing output ");
		personResults();
		log("Prepare output");
		SimulationContext context = createContext();
		log("Create context");
		
		printPerformance();
		
		return context;
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
		 network = NetworkSerializer.readVisumNetwork(configuration.getVisumFile());
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
		return new SimpleSimulationContext(configuration, experimentalParameters,  network, roadNetwork,
				dataRepository, simulationDays, format, resultWriter, electricChargingWriter,
				personResults);
	}

}
