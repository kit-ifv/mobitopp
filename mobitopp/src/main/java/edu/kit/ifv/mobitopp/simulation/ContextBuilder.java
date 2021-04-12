package edu.kit.ifv.mobitopp.simulation;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.Network;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.areatype.BicRepository;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.TypeMapping;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.ParserBuilder;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultMappings;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonChanger;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.util.StopWatch;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.reader.VisumNetworkReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextBuilder {

	private final StopWatch performanceLogger;
	private final SimulationParser format;
	private final AreaTypeRepository areaTypeRepository;
	private TypeMapping modeToType;
	private PersonChangerFactory personChangerFactory;
	private LanguageFactory languageFactory;
	private Function<ImpedanceIfc, ImpedanceIfc> wrapImpedance;

	private WrittenConfiguration configuration;
	private PersonChanger personChanger;
	private Predicate<HouseholdForSetup> householdFilter;
	private DynamicParameters experimentalParameters;
	private ResultWriter resultWriter;
	private ElectricChargingWriter electricChargingWriter;
	private SimulationDays simulationDays;
	private VisumNetwork network;
	private SimpleRoadNetwork roadNetwork;
	private PublicTransportData publicTransport;
	private DataRepositoryForSimulation dataRepository;
	private PersonResults personResults;
	private DynamicParameters destinationChoiceParameters;
	private DynamicParameters modeChoiceParameters;

	public ContextBuilder(AreaTypeRepository areaTypeRepository, TypeMapping modeToType,
			LanguageFactory language, PersonChangerFactory personChangerFactory) {
		super();
		this.areaTypeRepository = areaTypeRepository;
		this.modeToType = modeToType;
		this.languageFactory = language;
		this.personChangerFactory = personChangerFactory;
		this.wrapImpedance = Function.identity();
		performanceLogger = new StopWatch(LocalDateTime::now);
		ParserBuilder parser = new ParserBuilder();
		format = parser.forSimulation();

	}

	public ContextBuilder(AreaTypeRepository areaTypeRepository, TypeMapping modeToType,
			LanguageFactory language) {
		this(areaTypeRepository, modeToType, language, (c) -> PersonChanger.noChange());
	}

	public ContextBuilder(AreaTypeRepository areaTypeRepository) {
		this(areaTypeRepository, DefaultMappings.noAutonomousModes(), defaultLanguageFactory());
	}

	private static LanguageFactory defaultLanguageFactory() {
		return StandardNetfileLanguages::german;
	}

	public ContextBuilder() {
		this(new BicRepository());
	}

	public ContextBuilder use(TypeMapping modeToType) {
		this.modeToType = modeToType;
		return this;
	}

	public ContextBuilder use(PersonChangerFactory personChangerFactory) {
		this.personChangerFactory = personChangerFactory;
		return this;
	}

	public ContextBuilder use(LanguageFactory languageFactory) {
		this.languageFactory = languageFactory;
		return this;
	}

	public ContextBuilder use(Function<ImpedanceIfc, ImpedanceIfc> wrapImpedance) {
		this.wrapImpedance = wrapImpedance;
		return this;
	}
	
	public ContextBuilder addHouseholdFilterCondition(Predicate<HouseholdForSetup> filter) {
		if (this.householdFilter == null) {
			this.householdFilter = filter;
		} else {
			this.householdFilter = this.householdFilter.and(filter);
		}
		return this;
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
		createPersonChanger();
		createHouseholdFilter();
		experimentalParameters();
		destinationChoiceParameters();
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
		log.info("Runtimes while loading context:");
		performanceLogger.forEach((m, d) -> log.info(m + " " + d));
	}

	private void validateConfiguration() throws IOException {
		new Validate(modeToType).now(configuration);
		log("Validate configuration");
	}

	private void createPersonChanger() {
		personChanger = personChangerFactory.create(configuration);
	}

	private void createHouseholdFilter() {
		this.addHouseholdFilterCondition(new FilterFractionOfHouseholds(configuration.getFractionOfPopulation()));
	}

	private void experimentalParameters() {
		experimentalParameters = new DynamicParameters(configuration.getExperimental());
		log("Create experimental parameters");
	}

	private void destinationChoiceParameters() {
		destinationChoiceParameters = new DynamicParameters(configuration.getDestinationChoice());
		log("Create destination choice parameters");
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
		log.info("Reading VISUM network");
		if (null == network) {
			network = doLoadVisumNetwork(configuration.getVisumFile(), buildLanguage());
		}
	}

	private NetfileLanguage buildLanguage() {
		String carSystem = configuration.getVisumToMobitopp().getCarTransportSystemCode();
		String individualWalkSystem = configuration.getVisumToMobitopp()
				.getIndividualWalkTransportSystemCode();
		String publicTransportWalkSystem = configuration.getVisumToMobitopp()
				.getPtWalkTransportSystemCode();
		StandardNetfileLanguages builder = StandardNetfileLanguages.builder().carSystem(carSystem)
				.individualWalkSystem(individualWalkSystem)
				.publicTransportWalkSystem(publicTransportWalkSystem).build();
		return languageFactory.createFrom(builder);
	}

	protected VisumNetwork doLoadVisumNetwork(String fileName, NetfileLanguage language) {
		File visumFile = Convert.asFile(fileName);
		String ptSystemCode = configuration.getVisumToMobitopp().getPtTransportSystemCode();
		return new VisumNetworkReader(language).readNetwork(visumFile, ptSystemCode);
	}

	private void loadRoadNetwork() {
		loadVisumNetwork();
		log.info("creating road network");
		if (null == roadNetwork) {
			roadNetwork = doLoadRoadNetwork(network);
		}
	}

	private SimpleRoadNetwork doLoadRoadNetwork(VisumNetwork network) {
		return new SimpleRoadNetwork(network, carOf(network));
	}

	private VisumTransportSystem carOf(VisumRoadNetwork network) {
		VisumToMobitopp visumToMobitopp = configuration.getVisumToMobitopp();
		String carCode = visumToMobitopp.getCarTransportSystemCode();
		return network.transportSystems.getBy(carCode);
	}

	protected Network network() {
		loadRoadNetwork();
		return new Network(network, roadNetwork);
	}

	private void publicTransport() {
		log.info("reading PT network");
		publicTransport = configuration.getPublicTransport().loadData(this::network,
				simulationDays);
		log("Load public transport");
	}

	private void dataRepository() throws IOException {
		log.info("Loading data repository");
		int numberOfZones = configuration.getNumberOfZones();
		dataRepository = configuration.getDataSource().forSimulation(this::network, numberOfZones,
				simulationDays, publicTransport, resultWriter, electricChargingWriter,
				areaTypeRepository, modeToType, householdFilter, personChanger, wrapImpedance);
		log("Load data repository");
	}

	private void personResults() {
		log.info("Configuring output");
		personResults = createResults();
		log("Create output");
	}

	protected PersonResults createResults() {
		PersonResults results = new MultipleResults();
		results.addListener(new TripfileWriter(resultWriter, dataRepository.impedance()));
		return results;
	}

	private SimulationContext createContext() {
		SimpleSimulationContext context = new SimpleSimulationContext(configuration,
				experimentalParameters, dataRepository, simulationDays, format, resultWriter,
				electricChargingWriter, personResults, destinationChoiceParameters,
				modeChoiceParameters);
		log("Create context");
		return context;
	}

}
