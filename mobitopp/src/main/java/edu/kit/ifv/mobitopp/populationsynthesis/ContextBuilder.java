package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.StartDateSpecification;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.areatype.BicRepository;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.LocalPanelDataRepository;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.ParserBuilder;
import edu.kit.ifv.mobitopp.data.local.configuration.PopulationSynthesisParser;
import edu.kit.ifv.mobitopp.network.NetworkSerializer;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarSharingCustomerModel;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.SimulationDays;
import edu.kit.ifv.mobitopp.simulation.VisumToMobitopp;
import edu.kit.ifv.mobitopp.util.StopWatch;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNetworkReader;
import edu.kit.ifv.mobitopp.visum.VisumReader;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;

public class ContextBuilder {

  private final StopWatch performanceLogger;
  private final PopulationSynthesisParser format;

  private WrittenConfiguration configuration;
  private DynamicParameters experimentalParameters;
  private ResultWriter resultWriter;
  private AreaTypeRepository areaTypeRepository;
  private VisumNetwork network;
  private SimpleRoadNetwork roadNetwork;
  private DataRepositoryForPopulationSynthesis dataRepository;
  private Map<String, CarSharingCustomerModel> carSharing;
  private File carEngineFile;

  public ContextBuilder(AreaTypeRepository areaTypeReposirtory) {
    super();
    areaTypeRepository = areaTypeReposirtory;
    performanceLogger = new StopWatch(LocalDateTime::now);
    ParserBuilder parser = new ParserBuilder();
    format = parser.forPopulationSynthesis();
  }

  public ContextBuilder() {
    this(new BicRepository());
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

  private void visumNetwork() {
    network = NetworkSerializer.readVisumNetwork(configuration.getVisumFile());
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
    return new SimpleRoadNetwork(network, carOf(network));
  }

  private VisumTransportSystem carOf(VisumRoadNetwork network) {
    VisumToMobitopp visumToMobitopp = configuration.getVisumToMobitopp();
    String carCode = visumToMobitopp.getCarTransportSystemCode();
    return network.transportSystems.getBy(carCode);
  }

  private void dataRepository() throws IOException {
    int numberOfZones = configuration.getNumberOfZones();
    dataRepository = configuration
        .getDataSource()
        .forPopulationSynthesis(network, roadNetwork, demographyData(), panelData(), numberOfZones,
            startDate(), resultWriter, areaTypeRepository);
    log("Load data repository");
  }

  private DemographyData demographyData() {
    DemographyDataBuilder builder = new DemographyDataBuilder(configuration);
    DemographyData data = builder.build();
    log("Load demography data");
    return data;
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
    carSharing = new CarSharingParser(configuration.getCarSharing(), configuration.getSeed())
        .parse();
    log("Load car sharing");
  }

  private void carEngine() {
    carEngineFile = Convert.asFile(configuration.getCarOwnership().getEngine());
    log("Load car ownership model");
  }

  private SynthesisContext createContext() {
    SimpleSynthesisContext context = new SimpleSynthesisContext(configuration,
        experimentalParameters, network, roadNetwork, dataRepository, carSharing, carEngineFile,
        format, resultWriter);
    log("Create context");
    return context;
  }
}
