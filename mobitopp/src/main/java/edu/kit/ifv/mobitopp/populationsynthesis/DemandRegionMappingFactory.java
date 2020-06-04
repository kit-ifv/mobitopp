package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.DemandRegionMapping;

public class DemandRegionMappingFactory {

	public DemandRegionMapping create(Map<String, String> demandRegionMapping) {
		SortedMap<RegionalLevel, RegionalLevel> levelMapping = new TreeMap<>();
		Map<RegionalLevel, File> content = new LinkedHashMap<>();
		for (Entry<String, String> entry : demandRegionMapping.entrySet()) {
			String[] levels = entry.getKey().split(":");
			RegionalLevel region = RegionalLevel.levelOf(levels[0]);
			RegionalLevel part = RegionalLevel.levelOf(levels[1]);
			File file = Convert.asFile(entry.getValue());
			levelMapping.put(region, part);
			content.put(part, file);
		}
		return new StandardDemandRegionMapping(levelMapping, content);
	}

}
