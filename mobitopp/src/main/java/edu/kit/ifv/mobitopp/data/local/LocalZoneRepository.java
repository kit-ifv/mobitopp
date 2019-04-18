package edu.kit.ifv.mobitopp.data.local;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.dataimport.ZonesReaderCsvBased;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public class LocalZoneRepository implements ZoneRepository {

	private final Map<ZoneId, Zone> zones;
	private final Map<Integer, Zone> oidToZones;
	private final List<Zone> zonesAsList;
	private final Map<String, Integer> idToOid;

	LocalZoneRepository(Map<ZoneId, Zone> zones) {
		super();
		this.zones = zones;
		zonesAsList = asList(zones);
		oidToZones = createOidToZones(zones);
		this.idToOid = createIdToOidFrom(zonesAsList);
	}

	private Map<Integer, Zone> createOidToZones(Map<ZoneId, Zone> zones) {
    return zones
        .entrySet()
        .stream()
        .collect(toMap(e -> e.getKey().getMatrixColumn(), e -> e.getValue()));
  }

  private Map<String, Integer> createIdToOidFrom(List<Zone> zones) {
    return zones.stream().collect(toMap(z -> removeZonePrefix(z.getId()), Zone::getOid));
  }

  private static List<Zone> asList(Map<ZoneId, Zone> zones) {
		List<Zone> sorted = new ArrayList<>(zones.values());
		sorted.sort(comparing(Zone::getOid));
		return Collections.unmodifiableList(sorted);
	}
	
	@Override
	public boolean hasZone(int id) {
		return oidToZones.containsKey(id);
	}

	@Override
	public Zone getZoneByOid(int id) throws NoSuchElementException {
		if (oidToZones.containsKey(id)) {
			return oidToZones.get(id);
		}
		throw new IllegalArgumentException("No zone available for oid: " + id);
	}
	
	@Override
	public Zone getZoneById(ZoneId id) throws NoSuchElementException {
	  if (zones.containsKey(id)) {
	    return zones.get(id);
	  }
	  throw new IllegalArgumentException("No zone available for id: " + id);
	}
	
	@Override
	public List<ZoneId> getZoneIds() {
	  return new LinkedList<>(zones.keySet());
	}

	@Override
	public List<Zone> getZones() {
		return zonesAsList;
	}

	@Override
	public Map<ZoneId, Zone> zones() {
		return Collections.unmodifiableMap(zones);
	}

	public static ZoneRepository from(
			VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, ChargingType charging,
			DefaultPower defaultPower, File attractivityDataFile, AreaTypeRepository areaTypeRepository) {
		ZonesReaderCsvBased zonesReader = ZonesReaderCsvBased
				.from(visumNetwork, roadNetwork, charging, defaultPower, attractivityDataFile,
						areaTypeRepository);
		Map<ZoneId, Zone> mapping = new LocalZoneLoader(zonesReader).mapAllZones();
		return new LocalZoneRepository(mapping);
	}

	public static ZoneRepository from(List<Zone> zones) {
		Map<ZoneId, Zone> mapping = new LocalZoneLoader(() -> zones).mapAllZones();
		return new LocalZoneRepository(mapping);
	}

  public Integer map(String id) {
    return idToOid.get(removeZonePrefix(id));
  }

  private String removeZonePrefix(String id) {
    return id.replaceFirst("Z", "");
  }
  
}
