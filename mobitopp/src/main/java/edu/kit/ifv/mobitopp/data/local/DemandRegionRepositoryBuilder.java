package edu.kit.ifv.mobitopp.data.local;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandRegionRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.community.DemandRegionZoneMappingParser;
import edu.kit.ifv.mobitopp.populationsynthesis.community.DemandRegionMappingParser;
import edu.kit.ifv.mobitopp.populationsynthesis.community.DemographyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DemandRegionRepositoryBuilder {

	private final DemandRegionMapping demandRegionMapping;
	private final DemandZoneRepository demandZoneRepository;
	private final DemographyRepository demographyRepository;
	private Map<String, DemandRegion> last;

	public DemandRegionRepository create() {
		SortedMap<RegionalLevel, RegionalLevel> levels = demandRegionMapping.getLevels();
		last = Map.of();
		parseZoneMapping(levels);
		parseRegionMappings(levels);
		return new StandardDemandRegionRepository(last);
	}

	private void parseZoneMapping(SortedMap<RegionalLevel, RegionalLevel> levels) {
		RegionalLevel zone = RegionalLevel.zone;
		RegionalLevel regionLevel = levels.get(zone);
		DemandRegionZoneMappingParser parser = new DemandRegionZoneMappingParser(regionLevel,
				demandZoneRepository, demographyRepository);
		last = parser.parse(demandRegionMapping.contentFor(zone));
	}

	private void parseRegionMappings(Map<RegionalLevel, RegionalLevel> levels) {
		for (Entry<RegionalLevel, RegionalLevel> entry : levels.entrySet()) {
			DemandRegionMappingParser parser = new DemandRegionMappingParser(entry.getValue(),
					partMapping(entry.getKey()), demographyRepository);
			last = parser.parse(demandRegionMapping.contentFor(entry.getKey()));
		}
	}

	private Function<String, Optional<DemandRegion>> partMapping(RegionalLevel level) {
		if (RegionalLevel.zone.equals(level)) {
			return demandZoneRepository::getRegionByExternalId;
		}
		return id -> Optional.ofNullable(last.get(id));
	}

}
