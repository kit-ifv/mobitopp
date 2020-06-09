package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.DemographyRepository;
import edu.kit.ifv.mobitopp.data.local.DemandRegionMapping;
import edu.kit.ifv.mobitopp.data.local.StandardDemandRegionRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandRegionRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultRegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DemandRegionRepositoryBuilder {

	private final DemandRegionMapping demandRegionMapping;
	private final DemandZoneRepository demandZoneRepository;
	private final DemographyRepository demographyRepository;
	private Map<RegionalContext, DemandRegion> mapping;

	public DemandRegionRepository create() {
		SortedMap<RegionalLevel, RegionalLevel> levels = demandRegionMapping.getLowToHigh();
		mapping = new LinkedHashMap<>();
		addZones();
		parseZoneMapping(levels);
		parseRegionMappings(removeZone(levels));
		return new StandardDemandRegionRepository(mapping);
	}

	private void addZones() {
		demandZoneRepository.getZones().forEach(zone -> mapping.put(zone.getRegionalContext(), zone));
	}

	private SortedMap<RegionalLevel, RegionalLevel> removeZone(
			SortedMap<RegionalLevel, RegionalLevel> levels) {
		TreeMap<RegionalLevel, RegionalLevel> map = new TreeMap<>(levels);
		map.remove(RegionalLevel.zone);
		return map;
	}

	private void parseZoneMapping(SortedMap<RegionalLevel, RegionalLevel> levels) {
		RegionalLevel part = RegionalLevel.zone;
		if (!levels.containsKey(part)) {
			return;
		}
		RegionalLevel region = levels.get(part);
		DemandRegionZoneMappingParser parser = new DemandRegionZoneMappingParser(region,
				demandZoneRepository, demographyRepository);
		mapping.putAll(parser.parse(demandRegionMapping.contentFor(part)));
	}

	private void parseRegionMappings(Map<RegionalLevel, RegionalLevel> levels) {
		for (Entry<RegionalLevel, RegionalLevel> entry : levels.entrySet()) {
			RegionalLevel part = entry.getKey();
			RegionalLevel region = entry.getValue();
			DemandRegionMappingParser parser = new DemandRegionMappingParser(region, partMapping(part),
					demographyRepository);
			mapping.putAll(parser.parse(demandRegionMapping.contentFor(part)));
		}
	}

	private Function<String, Optional<DemandRegion>> partMapping(RegionalLevel level) {
		return id -> Optional.ofNullable(mapping.get(contextFor(level, id)));
	}

	private RegionalContext contextFor(RegionalLevel regionalLevel, String id) {
		return new DefaultRegionalContext(regionalLevel, id);
	}

}
