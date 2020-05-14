package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public interface DemandDataForCommunityCalculator {

	void calculateDemandData(Community community, ImpedanceIfc impedance);

}
