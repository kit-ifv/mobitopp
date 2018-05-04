package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public interface DemandDataForZoneCalculatorIfc {

	public void calculateDemandData(DemandZone zone, ImpedanceIfc impedance);

}
