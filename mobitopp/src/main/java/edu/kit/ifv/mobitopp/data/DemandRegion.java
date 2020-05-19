package edu.kit.ifv.mobitopp.data;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

public interface DemandRegion {
	
	String getExternalId();

	RegionalLevel regionalLevel();
	
	Demography nominalDemography();

	Demography actualDemography();

}