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
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public interface SynthesisContext {

	WrittenConfiguration configuration();

	DynamicParameters experimentalParameters();

	long seed();

  int maxIterations();

  double maxGoodnessDelta();

	File carEngineFile();

	VisumNetwork network();

	SimpleRoadNetwork roadNetwork();

	DemandZoneRepository zoneRepository();

	DataRepositoryForPopulationSynthesis dataRepository();

	ImpedanceIfc impedance();

	Map<String, MobilityProviderCustomerModel> carSharing();

	ResultWriter resultWriter();

	void printStartupInformationOn(PrintStream out);

  List<AttributeType> attributes(RegionalLevel level);

  PopulationSynthesisParser format();

}