package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;

public interface BikeSharingDataRepository {

	BikeSharingDataForZone getData(ZoneId zone);
	
}
