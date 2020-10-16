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

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.dataimport.DemographyBuilder;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;

public class LocalDemandZoneRepository implements DemandZoneRepository {

	private static final String shouldGeneratePopulation = "generatePopulation";
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
	public Optional<DemandRegion> getRegionByExternalId(String id) {
		return Optional.ofNullable(zonesByExternal.get(id));
	}

  @Override
  public DemandZone getRegionBy(final RegionalContext context) {
    if (context.matches(RegionalLevel.zone)) {
      return zoneByExternalId(context.externalId())
          .orElseThrow(() -> new IllegalArgumentException("Element not found: " + context.name()));
    }
    throw new IllegalArgumentException(String
        .format("Level of context must be %s but was %s.", RegionalLevel.zone, context.name()));
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
			ZoneRepository zoneRepository, DemographyData demographyData, int numberOfZones,
			StructuralData zoneProperties) {
		Function<Zone, DemandZone> toDemandZone = zone -> createZone(zone, demographyData,
				zoneProperties);
		List<DemandZone> demandZones = zoneRepository
				.getZones()
				.stream()
				.limit(numberOfZones)
				.map(toDemandZone)
				.collect(toList());
		Map<ZoneId, DemandZone> zones = demandZones
				.stream()
				.collect(toMap(DemandZone::getId, Function.identity(), StreamUtils.throwingMerger(),
						TreeMap::new));
		Map<String, DemandZone> zonesByExternal = demandZones
				.stream()
				.collect(toMap(d -> d.getId().getExternalId(), Function.identity(),
						StreamUtils.throwingMerger(), TreeMap::new));
		return new LocalDemandZoneRepository(zones, zonesByExternal, zoneRepository);
	}

	private static DemandZone createZone(
			Zone zone, DemographyData demographyData, StructuralData zoneProperties) {
		Demography demography = new DemographyBuilder(demographyData)
				.getDemographyFor(RegionalLevel.zone, idOf(zone));
		boolean generatePopulation = shouldGeneratePopulation(zone, zoneProperties);
		return new DemandZone(zone, demography, generatePopulation);
	}

	private static boolean shouldGeneratePopulation(Zone zone, StructuralData zoneProperties) {
		return zoneProperties.hasValue(idOf(zone), shouldGeneratePopulation)
				&& "1".equals(zoneProperties.getValue(idOf(zone), shouldGeneratePopulation));
	}

	private static String idOf(Zone zone) {
		return zone.getId().getExternalId();
	}
}
