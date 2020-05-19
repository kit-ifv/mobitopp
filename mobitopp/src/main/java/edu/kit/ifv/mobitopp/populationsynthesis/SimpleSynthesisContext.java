package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.PopulationSynthesisParser;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.MobilityProviderCustomerModel;
import edu.kit.ifv.mobitopp.populationsynthesis.community.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class SimpleSynthesisContext implements SynthesisContext {

  private final WrittenConfiguration configuration;
  private final DynamicParameters experimentalParameters;
  private final VisumNetwork visumNetwork;
  private final SimpleRoadNetwork roadNetwork;
  private final DataRepositoryForPopulationSynthesis dataRepository;
  private final ResultWriter resultWriter;
  private final Map<String, MobilityProviderCustomerModel> carSharing;
  private final PopulationSynthesisParser format;
  private final File carEngineFile;
	private final Map<RegionalLevel, List<AttributeType>> attributes;

	SimpleSynthesisContext(
			final WrittenConfiguration configuration, final DynamicParameters experimentalParameters,
			final VisumNetwork visumNetwork, final SimpleRoadNetwork roadNetwork,
			final DataRepositoryForPopulationSynthesis dataRepository,
			final Map<String, MobilityProviderCustomerModel> carSharing, final File carEngineFile,
			final Map<RegionalLevel, List<AttributeType>> attributes,
			final PopulationSynthesisParser format, final ResultWriter resultWriter) {
		super();
    this.configuration = configuration;
    this.experimentalParameters = experimentalParameters;
    this.visumNetwork = visumNetwork;
    this.roadNetwork = roadNetwork;
    this.dataRepository = dataRepository;
    this.carSharing = carSharing;
    this.carEngineFile = carEngineFile;
    this.attributes = attributes;
    this.format = format;
    this.resultWriter = resultWriter;
  }

  @Override
  public SimpleRoadNetwork roadNetwork() {
    return roadNetwork;
  }

  @Override
  public long seed() {
    return configuration().getSeed();
  }

  @Override
  public int maxIterations() {
    return configuration().getMaxIterations();
  }

  @Override
  public double maxGoodnessDelta() {
    return configuration().getMaxGoodnessDelta();
  }

  @Override
  public VisumNetwork network() {
    return visumNetwork;
  }

  @Override
  public File carEngineFile() {
    return carEngineFile;
  }

  @Override
  public ResultWriter resultWriter() {
    return resultWriter;
  }

  @Override
  public DataRepositoryForPopulationSynthesis dataRepository() {
    return dataRepository;
  }

  @Override
  public ImpedanceIfc impedance() {
    return dataRepository.impedance();
  }

  @Override
  public Map<String, MobilityProviderCustomerModel> carSharing() {
    return carSharing;
  }

  @Override
  public DemandZoneRepository zoneRepository() {
    return dataRepository.zoneRepository();
  }

  @Override
  public List<AttributeType> attributes(RegionalLevel level) {
    return attributes.get(level);
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
  public PopulationSynthesisParser format() {
    return format;
  }

  @Override
  public void printStartupInformationOn(PrintStream out) {
    out.println("Configuration:");
    out.print(format.serialise(configuration));
  }
}
