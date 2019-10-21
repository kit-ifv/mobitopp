package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public interface ParkingFacilityDataRepository {

	int getNumberOfParkingLots(VisumZone visumZone, ZoneId zoneId);

}
