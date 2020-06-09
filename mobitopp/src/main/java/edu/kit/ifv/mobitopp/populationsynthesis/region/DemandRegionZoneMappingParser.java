package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.toLinkedMap;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.community.DemographyRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.community.MultipleZones;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultRegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class DemandRegionZoneMappingParser {

	private static final String regionColumn = "regionId";
	private static final String zoneColumn = "partId";
	
	private final RegionalLevel regionalLevel;
	private final DemandZoneRepository zoneRepository;
	private final DemographyRepository demographyRepository;
	private final Map<String, DemandRegionZones> zones;

	public DemandRegionZoneMappingParser(
			final RegionalLevel regionLevel, final DemandZoneRepository zoneRepository,
			final DemographyRepository demographyRepository) {
		super();
		this.regionalLevel = regionLevel;
		this.zoneRepository = zoneRepository;
		this.demographyRepository = demographyRepository;
		zones = new LinkedHashMap<>();
	}

	public Map<RegionalContext, DemandRegion> parse(final File mappingFile) {
		Stream<Row> input = load(mappingFile);
		return parse(input);
	}

	private Stream<Row> load(final File mappingFile) {
		return CsvFile.createFrom(mappingFile).stream();
	}

	public Map<RegionalContext, DemandRegion> parse(Stream<Row> mapping) {
		mapping.forEach(this::addRelation);
		return zones
				.entrySet()
				.stream()
				.collect(toLinkedMap(e -> contextFor(e.getKey()),
						e -> e.getValue().build(demographyRepository)));
	}

	private RegionalContext contextFor(String id) {
		return new DefaultRegionalContext(regionalLevel, id);
	}

	private void addRelation(final Row row) {
		String regionId = row.get(regionColumn);
		String zoneId = row.get(zoneColumn);
		Optional<DemandZone> zone = zoneRepository.zoneByExternalId(zoneId);
		DemandRegionZones region = getRegion(regionId);
		zone.ifPresent(region.zones::add);
	}

	private DemandRegionZones getRegion(final String regionId) {
		if (zones.containsKey(regionId)) {
			return zones.get(regionId);
		}
		DemandRegionZones region = new DemandRegionZones(regionId, regionalLevel);
		zones.put(regionId, region);
		return region;
	}

	private static final class DemandRegionZones {

		private final String id;
		private final RegionalLevel regionalLevel;
		private final List<DemandZone> zones;

		public DemandRegionZones(final String id, final RegionalLevel regionalLevel) {
			super();
			this.id = id;
			this.regionalLevel = regionalLevel;
			zones = new LinkedList<>();
		}

		public DemandRegion build(DemographyRepository demographyRepository) {
			Demography demography = demographyRepository.getDemographyFor(regionalLevel, id);
			return new MultipleZones(id, regionalLevel, demography, zones);
		}
	}

}
