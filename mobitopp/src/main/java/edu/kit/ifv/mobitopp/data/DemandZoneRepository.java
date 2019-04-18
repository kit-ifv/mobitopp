package edu.kit.ifv.mobitopp.data;

import java.util.List;

public interface DemandZoneRepository {

	DemandZone zoneById(ZoneId id);

	List<DemandZone> getZones();

	ZoneRepository zoneRepository();

}
