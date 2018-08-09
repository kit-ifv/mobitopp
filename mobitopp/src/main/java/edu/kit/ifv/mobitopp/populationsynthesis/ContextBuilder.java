package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.StartDateSpecification;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.LocalPanelDataRepository;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.ParserBuilder;
import edu.kit.ifv.mobitopp.data.local.configuration.PopulationSynthesisParser;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.SimulationDays;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCustomerModel;
import edu.kit.ifv.mobitopp.util.StopWatch;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNetworkReader;
import edu.kit.ifv.mobitopp.visum.VisumReader;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;

public class ContextBuilder {

	private final StopWatch performanceLogger;
	private final PopulationSynthesisParser format;

	private WrittenConfiguration configuration;
	private DynamicParameters experimentalParameters;
	private ResultWriter resultWriter;
	private VisumNetwork network;
	private SimpleRoadNetwork roadNetwork;
	private DataRepositoryForPopulationSynthesis dataRepository;
	private ActivityScheduleCreator activityScheduleCreator;
	private Map<String, CarSharingCustomerModel> carSharing;
	private File carEngineFile;

	public ContextBuilder() {
		super();
		performanceLogger = new StopWatch(LocalDateTime::now);
		ParserBuilder parser = new ParserBuilder();
		format = parser.forPopulationSynthesis();
	}

	public SynthesisContext buildFrom(File configurationFile) throws IOException {
		WrittenConfiguration configuration = format.parse(configurationFile);
		return buildFrom(configuration);
	}

	public SynthesisContext buildFrom(WrittenConfiguration configuration) throws IOException {
		this.configuration = configuration;
		return loadData();
	}

	private SynthesisContext loadData() throws IOException {
		startLoading();
		validateConfiguration();
		experimentalParameters();
		resultWriter();
		visumNetwork();
		roadNetwork();
		dataRepository();
		activityScheduleCreator();
		carSharing();
		carEngine();
		SynthesisContext context = createContext();
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
		log("Validate configuration");
	}

	private void experimentalParameters() {
		experimentalParameters = new DynamicParameters(configuration.getExperimental());
		log("Create experimental parameters");
	}

	private void resultWriter() {
		File inBaseFolder = Convert.asFile(configuration.getResultFolder());
		resultWriter = ResultWriter.create(inBaseFolder);
		log("Configure result writer");
	}

	private void visumNetwork() {
		VisumReader reader = new VisumReader();
		VisumNetworkReader networkReader = createNetworkReader(reader);
		File visumFile = Convert.asFile(configuration.getVisumFile());
		network = networkReader.readNetwork(visumFile);
		log("Load visum network");
	}

	protected VisumNetworkReader createNetworkReader(VisumReader reader) {
		return new VisumNetworkReader(reader);
	}

	private void roadNetwork() {
		roadNetwork = createRoadNetwork(network);
		log("Create road network");
	}

	protected SimpleRoadNetwork createRoadNetwork(VisumRoadNetwork network) {
		return new SimpleRoadNetwork(network);
	}

	private void dataRepository() throws IOException {
		int numberOfZones = configuration.getNumberOfZones();
		dataRepository = configuration.getDataSource().forPopulationSynthesis(network, roadNetwork,
				demographyData(), panelData(), numberOfZones, startDate(), resultWriter);
		log("Load data repository");
	}

	private StructuralData demographyData() {
		File demographyDataFile = Convert.asFile(configuration.getDemographyData());
		StructuralData structuralData = new StructuralData(new CsvFile(demographyDataFile.getAbsolutePath()));
		log("Load demography data");
		return structuralData;
	}

	private PanelDataRepository panelData() {
		File panelDataFile = Convert.asFile(configuration.getPanelData());
		PanelDataRepository panelData = LocalPanelDataRepository.loadFrom(panelDataFile);
		log("Load panel data");
		return panelData;
	}

	private StartDateSpecification startDate() {
		return SimulationDays::simulationStart;
	}

	private void activityScheduleCreator() {
		activityScheduleCreator = configuration.getActivityScheduleCreator().create(
				configuration.getSeed());
		log("Create activity schedule creator");
	}

	private void carSharing() {
		carSharing = new CarSharingParser(configuration.getCarSharing(), configuration.getSeed())
				.parse();
		log("Load car sharing");
	}

	private void carEngine() {
		carEngineFile = Convert.asFile(configuration.getCarOwnership().getEngine());
		log("Load car ownership model");
	}

	private SynthesisContext createContext() {
		SimpleSynthesisContext context = new SimpleSynthesisContext(configuration, experimentalParameters, network, roadNetwork,
				dataRepository, activityScheduleCreator, carSharing, carEngineFile, format, resultWriter);
		log("Create context");
		return context;
	}
}
