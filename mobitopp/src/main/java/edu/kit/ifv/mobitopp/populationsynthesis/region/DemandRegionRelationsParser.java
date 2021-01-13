package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandRegionRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemandRegionRelationsParser {

	private final DemandRegionRepository repository;
	private final Map<DemandRegion, Map<DemandRegion, Integer>> relations;
	private final RegionalLevel level;
	private final BiPredicate<DemandRegion, DemandRegion> predicate;

	public DemandRegionRelationsParser(final RegionalLevel level,
		final DemandRegionRepository repository,
		final BiPredicate<DemandRegion, DemandRegion> predicate) {
		super();
		this.level = level;
		this.repository = repository;
		this.predicate = predicate;
		this.relations = new LinkedHashMap<>();
	}

	public DemandRegionRelationsParser(final RegionalLevel level,
		final DemandRegionRepository repository) {
		this(
			level,
			repository,
			(origin, destination) -> true);
	}

	public DemandRegionRelationsRepository parse(File input) {
		load(input).forEach(this::parse);
		Collection<DemandRegion> regions = repository.getRegionsOf(level);
		return new StandardDemandRegionRelationsRepository(getParsedRelations(), regions);
	}

	Map<DemandRegion, Map<DemandRegion, Integer>> getParsedRelations() {
		return Collections.unmodifiableMap(relations);
	}

	Stream<Row> load(File input) {
		return CsvFile.createFrom(input).stream();
	}

	private void parse(Row row) {
		String originId = row.get("origin");
		String destinationId = row.get("destination");
		Optional<DemandRegion> origin = repository.getRegionWith(level, originId);
		Optional<DemandRegion> destination = repository.getRegionWith(level, destinationId);
		if (origin.isEmpty() || destination.isEmpty()) {
			log
				.warn("Could not find origin {} or destination {} to parse demand region relation.",
					originId, destinationId);
			return;
		}
		int commuters = getCommuters(row, origin, destination);
		getMapping(origin.get()).put(destination.get(), commuters);
	}

	private int getCommuters(Row row, Optional<DemandRegion> origin,
		Optional<DemandRegion> destination) {
		if (predicate.test(origin.get(), destination.get())) {
			return row.valueAsInteger("commuters");
		}
		return 0;
	}

	private Map<DemandRegion, Integer> getMapping(DemandRegion origin) {
		if (!relations.containsKey(origin)) {
			relations.put(origin, new LinkedHashMap<>());
		}
		return relations.get(origin);
	}

}
