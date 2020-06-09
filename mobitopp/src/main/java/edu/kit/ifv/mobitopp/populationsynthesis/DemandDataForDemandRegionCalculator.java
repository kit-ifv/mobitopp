package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public interface DemandDataForDemandRegionCalculator {

	void calculateDemandData(DemandRegion region, ImpedanceIfc impedance);

}
