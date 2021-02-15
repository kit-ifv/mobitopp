package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.Map;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.dataimport.ZonesReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalZoneLoader {

  private static final int allZones = Integer.MAX_VALUE;
  private final ZonesReader zonesReader;

  public LocalZoneLoader(ZonesReader zonesReader) {
    this.zonesReader = zonesReader;
  }

  public Map<ZoneId, Zone> mapZones(int maximumNumberOfZones) {
    Map<ZoneId, Zone> mapping = new TreeMap<>();
    for (Zone zone : zonesReader.getZones()) {
      if (maximumNumberOfZones <= mapping.size()) {
        break;
      }
      if (mapping.containsKey(zone.getId())) {
        throw warn(new IllegalArgumentException(
            "Mapping already contains zone with id: " + zone.getId()), log);
      }
      mapping.put(zone.getId(), zone);
    }
    return mapping;
  }

  public Map<ZoneId, Zone> mapAllZones() {
    return mapZones(allZones);
  }

}
