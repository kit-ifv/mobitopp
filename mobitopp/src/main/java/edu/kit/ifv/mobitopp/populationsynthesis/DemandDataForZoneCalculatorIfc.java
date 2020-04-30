package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public interface DemandDataForZoneCalculatorIfc {

	void calculateDemandData(DemandZone zone, ImpedanceIfc impedance);

	/**
	 * Save the demand data 
	 * @deprecated This is for backward compatibility for the ipf based population synthesis
	 * @param zone
	 */
	default void saveDemandData(DemandZone zone) {
		
	}

}
