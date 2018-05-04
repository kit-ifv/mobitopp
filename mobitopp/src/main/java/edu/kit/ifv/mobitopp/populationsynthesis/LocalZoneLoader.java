package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Map;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.dataimport.ZonesReader;

public class LocalZoneLoader {

	private final ZonesReader zonesReader;

	public LocalZoneLoader(ZonesReader zonesReader) {
		this.zonesReader = zonesReader;
	}

	public Map<Integer, Zone> mapZones(int maximumNumberOfZones) {
		Map<Integer, Zone> mapping = new TreeMap<>();
		for (Zone zone : zonesReader.getZones()) {
			if (maximumNumberOfZones <= mapping.size()) {
				break;
			}
			if (mapping.containsKey(zone.getOid())) {
				throw new IllegalArgumentException("Mapping already contains zone with oid: " + zone.getOid());
			}
			mapping.put(zone.getOid(), zone);
		}
		return mapping;
	}

}
