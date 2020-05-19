package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.StartDateSpecification;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.areatype.BicRepository;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.LocalPanelDataRepository;
import edu.kit.ifv.mobitopp.data.local.TypeMapping;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.ParserBuilder;
import edu.kit.ifv.mobitopp.data.local.configuration.PopulationSynthesisParser;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.MobilityProviderCustomerModel;
import edu.kit.ifv.mobitopp.populationsynthesis.community.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonChanger;
import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.LanguageFactory;
import edu.kit.ifv.mobitopp.simulation.SimulationDays;
import edu.kit.ifv.mobitopp.simulation.VisumToMobitopp;
import edu.kit.ifv.mobitopp.util.StopWatch;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.reader.VisumNetworkReader;

public class ContextBuilder {

  private final StopWatch performanceLogger;
  private final PopulationSynthesisParser format;
  private final AreaTypeRepository areaTypeRepository;
  private final TypeMapping modeToType;
  private PersonChanger personChanger;
  private LanguageFactory languageFactory;
  private Function<ImpedanceIfc, ImpedanceIfc> wrapImpedance;

  private WrittenConfiguration configuration;
  private DynamicParameters experimentalParameters;
  private ResultWriter resultWriter;
  private VisumNetwork network;
  private SimpleRoadNetwork roadNetwork;
  private DataRepositoryForPopulationSynthesis dataRepository;
  private Map<String, MobilityProviderCustomerModel> carSharing;
  private File carEngineFile;
  private DemographyData demographyData;
	private StructuralData zonePropertiesData;

	public ContextBuilder(
			final AreaTypeRepository areaTypeReposirtory, final TypeMapping modeToType,
			final LanguageFactory languageFactory, final PersonChanger personChanger) {
		super();
		areaTypeRepository = areaTypeReposirtory;
		this.modeToType = modeToType;
		this.languageFactory = languageFactory;
		this.personChanger = personChanger;
		this.wrapImpedance = Function.identity();
		performanceLogger = new StopWatch(LocalDateTime::now);
		ParserBuilder parser = new ParserBuilder();
		format = parser.forPopulationSynthesis();
	}
	
	public ContextBuilder(
			AreaTypeRepository areaTypeReposirtory, TypeMapping modeToType,
			LanguageFactory netfileLanguage) {
		this(areaTypeReposirtory, modeToType, netfileLanguage, PersonChanger.noChange());
	}

	public ContextBuilder(AreaTypeRepository areaTypeRepository) {
		this(areaTypeRepository, DefaultMappings.noAutonomousModes(), StandardNetfileLanguages::german);
	}

	public ContextBuilder() {
		this(new BicRepository(), DefaultMappings.noAutonomousModes(),
				StandardNetfileLanguages::german);
	}
  
  public ContextBuilder use(PersonChanger personChanger) {
  	this.personChanger = personChanger;
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
    zoneProperties();
    visumNetwork();
    roadNetwork();
    demography();
    dataRepository();
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
    performanceLogger.forEach((m, d) -> System.out.println(m + " " + d));
  }

  private void validateConfiguration() throws IOException {
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
  
  private void zoneProperties() {
  	File file = Convert.asFile(configuration.getSynthesisZoneProperties());
    zonePropertiesData = new StructuralData(CsvFile.createFrom(file));
    log("Configure result writer");
  }

  private void visumNetwork() {
    network = loadNetwork();
    log("Load visum network");
  }

  protected VisumNetwork loadNetwork() {
    File visumFile = Convert.asFile(configuration.getVisumFile());
    String ptSystemCode = configuration.getVisumToMobitopp().getPtTransportSystemCode();
		return new VisumNetworkReader(buildLanguage()).readNetwork(visumFile, ptSystemCode);
  }

	private NetfileLanguage buildLanguage() {
		String carSystem = configuration.getVisumToMobitopp().getCarTransportSystemCode();
		String individualWalkSystem = configuration.getVisumToMobitopp().getIndividualWalkTransportSystemCode();
		String publicTransportWalkSystem = configuration.getVisumToMobitopp().getPtWalkTransportSystemCode();
		StandardNetfileLanguages builder = StandardNetfileLanguages
				.builder()
				.carSystem(carSystem)
				.individualWalkSystem(individualWalkSystem)
				.publicTransportWalkSystem(publicTransportWalkSystem)
				.build();
		return languageFactory.createFrom(builder);
	}

  private void roadNetwork() {
    roadNetwork = createRoadNetwork(network);
    log("Create road network");
  }

  protected SimpleRoadNetwork createRoadNetwork(VisumRoadNetwork network) {
    return new SimpleRoadNetwork(network, carOf(network));
  }

  private VisumTransportSystem carOf(VisumRoadNetwork network) {
    VisumToMobitopp visumToMobitopp = configuration.getVisumToMobitopp();
    String carCode = visumToMobitopp.getCarTransportSystemCode();
    return network.transportSystems.getBy(carCode);
  }

  private void demography() {
    Logger logger = zoneId -> System.out.println("Missing zone with ID: " + zoneId);
    demographyData = demographyData(network, logger);
    log("Load demography data");
  }

  private DemographyData demographyData(VisumNetwork network, Logger logger) {
    DemographyDataBuilder builder = new DemographyDataBuilder(configuration);
    DemographyData data = builder.build();
    verify(data, network, logger);
    log("Load demography data");
    return data;
  }

  private void dataRepository() throws IOException {
    int numberOfZones = configuration.getNumberOfZones();
    dataRepository = configuration
        .getDataSource()
        .forPopulationSynthesis(network, roadNetwork, demographyData, zonePropertiesData, panelData(), numberOfZones,
            startDate(), resultWriter, areaTypeRepository, modeToType, personChanger, wrapImpedance);
    log("Load data repository");
  }

  private void verify(DemographyData data, VisumNetwork network, Logger logger) {
    List<Integer> zoneIds = network.zones.values().stream().map(zone -> zone.id).collect(toList());
    new DemographyChecker(zoneIds, logger::println).check(data);
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

	private void carSharing() {
		carSharing = new CarSharingParser(configuration.getMobilityProviders(), configuration.getSeed())
				.parse();
		log("Load car sharing");
	}

  private void carEngine() {
    carEngineFile = Convert.asFile(configuration.getCarOwnership().getEngine());
    log("Load car ownership model");
  }

	private SynthesisContext createContext() {
		Map<RegionalLevel, List<AttributeType>> attributes = demographyData.allAttributes();
		SimpleSynthesisContext context = new SimpleSynthesisContext(configuration,
				experimentalParameters, network, roadNetwork, dataRepository, carSharing, carEngineFile,
				attributes, format, resultWriter);
		log("Create context");
		return context;
	}
}
