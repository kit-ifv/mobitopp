package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class DemandRegionMappingParser {

	static final String regionColumn = "regionId";
	static final String partColumn = "partId";
	
	private final RegionalLevel regionalLevel;
	private final Function<String, Optional<DemandRegion>> partRepository;
	private final DemographyRepository demographyRepository;
	private final Map<String, DemandRegionParts> regionToPart;

	public DemandRegionMappingParser(
			RegionalLevel regionalLevel, final Function<String, Optional<DemandRegion>> partRepository,
			final DemographyRepository demographyRepository) {
		super();
		this.regionalLevel = regionalLevel;
		this.partRepository = partRepository;
		this.demographyRepository = demographyRepository;
		regionToPart = new LinkedHashMap<>();
	}

	public Map<String, DemandRegion> parse(final File mappingFile) {
		Stream<Row> input = load(mappingFile);
		return parse(input);
	}

	private Stream<Row> load(final File mappingFile) {
		return CsvFile.createFrom(mappingFile).stream();
	}

	public Map<String, DemandRegion> parse(Stream<Row> mapping) {
		mapping.forEach(this::addCommunityRelation);
		return regionToPart
				.entrySet()
				.stream()
				.collect(toMap(e -> e.getKey(), e -> e.getValue().build(demographyRepository)));
	}

	private void addCommunityRelation(final Row row) {
		String regionId = row.get(regionColumn);
		String partId = row.get(partColumn);
		Optional<DemandRegion> zone = partRepository.apply(partId);
		DemandRegionParts region = getRegion(regionId);
		zone.ifPresent(region.parts::add);
	}

	private DemandRegionParts getRegion(final String regionId) {
		if (regionToPart.containsKey(regionId)) {
			return regionToPart.get(regionId);
		}
		DemandRegionParts region = new DemandRegionParts(regionId, regionalLevel);
		regionToPart.put(regionId, region);
		return region;
	}

	private static final class DemandRegionParts {

		private final String id;
		private final RegionalLevel level;
		private final List<DemandRegion> parts;

		public DemandRegionParts(final String id, final RegionalLevel level) {
			super();
			this.id = id;
			this.level = level;
			parts = new LinkedList<>();
		}

		public DemandRegion build(DemographyRepository demographyRepository) {
			Demography demography = demographyRepository.getDemographyFor(level, id);
			return new MultipleRegions(id, level, demography, parts);
		}
	}

}
