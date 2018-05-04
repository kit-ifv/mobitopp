package edu.kit.ifv.mobitopp.data;

import java.util.List;

public interface DemandZoneRepository {

	DemandZone zoneByOid(int oid);

	List<DemandZone> getZones();

	ZoneRepository zoneRepository();

}
