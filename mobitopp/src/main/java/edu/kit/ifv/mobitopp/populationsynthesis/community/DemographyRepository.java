package edu.kit.ifv.mobitopp.populationsynthesis.community;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

public interface DemographyRepository {

	Demography getDemographyFor(RegionalLevel level, String id);

}
