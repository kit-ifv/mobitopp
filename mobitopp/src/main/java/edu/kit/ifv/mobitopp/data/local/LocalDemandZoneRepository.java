package edu.kit.ifv.mobitopp.data.local;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.dataimport.DemographyBuilder;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;

public class LocalDemandZoneRepository implements DemandZoneRepository {

	private final Map<Integer, DemandZone> zones;
	private final List<DemandZone> zonesAsList;
	private final ZoneRepository zoneRepository;

	public LocalDemandZoneRepository(Map<Integer, DemandZone> zones, ZoneRepository zoneRepository) {
		super();
		this.zones = zones;
		this.zoneRepository = zoneRepository;
		zonesAsList = asList(zones);
	}

	private static List<DemandZone> asList(Map<Integer, DemandZone> zones) {
		List<DemandZone> sorted = new ArrayList<>(zones.values());
		sorted.sort(comparing(DemandZone::getOid));
		return Collections.unmodifiableList(sorted);
	}

	@Override
	public DemandZone zoneByOid(int oid) {
		return zones.get(oid);
	}

	@Override
	public List<DemandZone> getZones() {
		return zonesAsList;
	}

	@Override
	public ZoneRepository zoneRepository() {
		return zoneRepository;
	}

	public static DemandZoneRepository from(
			ZoneRepository zoneRepository, StructuralData structuralData) {
		Map<String, Zone> idToZone = zoneRepository.getZones().stream().collect(
				toMap(Zone::getId, Function.identity()));
		Map<Integer, DemandZone> zones = new TreeMap<>();
		structuralData.resetIndex();
		while(structuralData.hasNext()) {
			DemandZone zone = createZone(idToZone, structuralData);
			zones.put(zone.getOid(), zone);
			structuralData.next();
		}
		return new LocalDemandZoneRepository(zones, zoneRepository);
	}

	private static DemandZone createZone(
			Map<String, Zone> idToZone, StructuralData structuralData) {
		Zone zone = idToZone.get("Z" + structuralData.currentZone());
		Demography demography = new DemographyBuilder(structuralData).build();
		return new DemandZone(zone, demography);
	}
}
