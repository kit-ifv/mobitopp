package edu.kit.ifv.mobitopp.data.local;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.dataimport.DemographyBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;

public class LocalDemandZoneRepository implements DemandZoneRepository {

  private final Map<ZoneId, DemandZone> zones;
  private final List<DemandZone> zonesAsList;
  private final ZoneRepository zoneRepository;

  public LocalDemandZoneRepository(Map<ZoneId, DemandZone> zones, ZoneRepository zoneRepository) {
    super();
    this.zones = zones;
    this.zoneRepository = zoneRepository;
    zonesAsList = asList(zones);
  }

  private static List<DemandZone> asList(Map<ZoneId, DemandZone> zones) {
    List<DemandZone> sorted = new ArrayList<>(zones.values());
    sorted.sort(comparing(DemandZone::getId));
    return Collections.unmodifiableList(sorted);
  }

  @Override
  public DemandZone zoneById(ZoneId id) {
    return zones.get(id);
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
    Map<ZoneId, DemandZone> zones = zoneRepository
        .getZones()
        .stream()
        .limit(numberOfZones)
        .map(toDemandZone)
        .collect(toMap(DemandZone::getId, Function.identity(), throwingMerger(), TreeMap::new));
    return new LocalDemandZoneRepository(zones, zoneRepository);
  }

  private static BinaryOperator<DemandZone> throwingMerger() {
    return (u, v) -> {
      throw new IllegalStateException(String.format("Duplicate key: %s and %s", u, v));
    };
  }

  private static DemandZone createZone(Zone zone, DemographyData demographyData) {
    Demography demography = new DemographyBuilder(demographyData).build(idOf(zone));
    return new DemandZone(zone, demography);
  }

  private static String idOf(Zone zone) {
    return zone.getId().getExternalId();
  }
}
