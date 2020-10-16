package edu.kit.ifv.mobitopp.data;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;

public interface DemandZoneRepository {

	Optional<DemandZone> zoneById(ZoneId id);

	Optional<DemandZone> zoneByExternalId(String zoneId);

	List<DemandZone> getZones();

	ZoneRepository zoneRepository();

	Optional<DemandRegion> getRegionByExternalId(String id);

  DemandZone getRegionBy(RegionalContext context);

}
