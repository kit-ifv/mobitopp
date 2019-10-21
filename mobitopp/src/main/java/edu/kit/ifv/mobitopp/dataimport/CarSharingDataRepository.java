package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public interface CarSharingDataRepository {

	CarSharingDataForZone getData(VisumZone visumZone, ZonePolygon polygon, Zone zone);

}
