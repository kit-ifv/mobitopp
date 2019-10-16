package edu.kit.ifv.mobitopp.data.local;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.dataimport.DemographyBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;

public class LocalDemandZoneRepository implements DemandZoneRepository {

	private final Map<ZoneId, DemandZone> zones;
	private final Map<String, DemandZone> zonesByExternal;
	private final List<DemandZone> zonesAsList;
	private final ZoneRepository zoneRepository;

	public LocalDemandZoneRepository(
			Map<ZoneId, DemandZone> zones, Map<String, DemandZone> zonesByExternal,
			ZoneRepository zoneRepository) {
		super();
		this.zones = zones;
		this.zonesByExternal = zonesByExternal;
		this.zoneRepository = zoneRepository;
		zonesAsList = asList(zones);
	}

	private static List<DemandZone> asList(Map<ZoneId, DemandZone> zones) {
		List<DemandZone> sorted = new ArrayList<>(zones.values());
		sorted.sort(comparing(DemandZone::getId));
		return Collections.unmodifiableList(sorted);
	}

	@Override
	public Optional<DemandZone> zoneById(ZoneId id) {
		return Optional.ofNullable(zones.get(id));
	}
	
	@Override
	public Optional<DemandZone> zoneByExternalId(String zoneId) {
		return Optional.ofNullable(zonesByExternal.get(zoneId));
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
			ZoneRepository zoneRepository, DemographyData demographyData, int numberOfZones) {
		Function<Zone, DemandZone> toDemandZone = zone -> createZone(zone, demographyData);
		List<DemandZone> demandZones = zoneRepository
				.getZones()
				.stream()
				.limit(numberOfZones)
				.map(toDemandZone).collect(toList());
		Map<ZoneId, DemandZone> zones = demandZones.stream()
				.collect(toMap(DemandZone::getId, Function.identity(), StreamUtils.throwingMerger(),
						TreeMap::new));
		Map<String, DemandZone> zonesByExternal = demandZones.stream()
				.collect(toMap(d -> d.getId().getExternalId(), Function.identity(),
						StreamUtils.throwingMerger(), TreeMap::new));
		return new LocalDemandZoneRepository(zones, zonesByExternal, zoneRepository);
	}

	private static DemandZone createZone(Zone zone, DemographyData demographyData) {
		Demography demography = new DemographyBuilder(demographyData).build(idOf(zone));
		return new DemandZone(zone, demography);
	}

	private static String idOf(Zone zone) {
		return zone.getId().getExternalId();
	}
}
