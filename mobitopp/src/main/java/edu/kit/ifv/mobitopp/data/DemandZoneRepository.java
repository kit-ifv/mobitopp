package edu.kit.ifv.mobitopp.data;

import java.util.List;
import java.util.Optional;

public interface DemandZoneRepository {

	Optional<DemandZone> zoneById(ZoneId id);

	Optional<DemandZone> zoneByExternalId(String zoneId);

	List<DemandZone> getZones();

	ZoneRepository zoneRepository();

}
