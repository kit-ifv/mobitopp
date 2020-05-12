package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public interface BikeSharingDataRepository {

	BikeSharingDataForZone getData(VisumZone visumZone, ZonePolygon polygon, Zone zone);
	
}
