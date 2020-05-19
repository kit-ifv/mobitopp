package edu.kit.ifv.mobitopp.data;

import java.io.IOException;
import java.util.Map;

import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.community.DemographyRepository;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public interface DataRepositoryForPopulationSynthesis {

	Map<ActivityType, FixedDistributionMatrix> fixedDistributionMatrices();
  
  DemographyRepository demographyRepository();

	DemandDataRepository demandDataRepository();

	DemandZoneRepository zoneRepository();

	ImpedanceIfc impedance();

	PanelDataRepository panelDataRepository();

	void finishExecution() throws IOException;

}
