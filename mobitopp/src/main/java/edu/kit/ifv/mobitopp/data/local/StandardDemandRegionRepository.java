package edu.kit.ifv.mobitopp.data.local;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandRegionRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultRegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StandardDemandRegionRepository implements DemandRegionRepository {

	private final Map<RegionalContext, DemandRegion> mapping;

	@Override
	public List<DemandRegion> getRegionsOf(RegionalLevel level) {
		return mapping
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().matches(level))
				.map(Entry::getValue)
				.collect(toList());
	}

	@Override
	public Optional<DemandRegion> getRegionWith(RegionalLevel level, String id) {
		return getRegionWith(new DefaultRegionalContext(level, id));
	}

	public Optional<DemandRegion> getRegionWith(RegionalContext context) {
		return Optional.ofNullable(mapping.get(context));
	}

}
