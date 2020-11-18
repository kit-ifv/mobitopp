package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandRegionRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class DemandRegionRelationsParser {

	private final DemandRegionRepository repository;
	private final Map<DemandRegion, Map<DemandRegion, Integer>> relations;
	private final RegionalLevel level;

	public DemandRegionRelationsParser(RegionalLevel level, DemandRegionRepository repository) {
		super();
		this.level = level;
		this.repository = repository;
		this.relations = new LinkedHashMap<>();
	}

	public DemandRegionRelationsRepository parse(File input) {
		load(input).forEach(this::parse);
		return new StandardDemandRegionRelationsRepository(Collections.unmodifiableMap(relations));
	}

	Stream<Row> load(File input) {
		return CsvFile.createFrom(input).stream();
	}
	
	private void parse(Row row) {
		Optional<DemandRegion> origin = repository.getRegionWith(level, row.get("origin"));
		Optional<DemandRegion> destination = repository.getRegionWith(level, row.get("destination"));
		if (origin.isEmpty() || destination.isEmpty()) {
		  return;
		}
		int commuters = row.valueAsInteger("commuters");
		getMapping(origin.get()).put(destination.get(), commuters);
	}

	private Map<DemandRegion, Integer> getMapping(DemandRegion origin) {
		if (!relations.containsKey(origin)) {
			relations.put(origin, new LinkedHashMap<>());
		}
		return relations.get(origin);
	}

}
