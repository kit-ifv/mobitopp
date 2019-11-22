package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

public class CommunityZoneMappingParser {

	private final DemandZoneRepository zoneRepository;
	private final Map<String, CommunityZones> communityZones;

	public CommunityZoneMappingParser(final DemandZoneRepository zoneRepository) {
		super();
		this.zoneRepository = zoneRepository;
		communityZones = new LinkedHashMap<>();
	}

	public Map<String, Community> parse(final File mappingFile) {
		load(mappingFile).forEach(this::addCommunityRelation);
		return communityZones
				.entrySet()
				.stream()
				.collect(toMap(e -> e.getKey(), e -> e.getValue().build()));
	}

	Stream<Row> load(final File mappingFile) {
		return CsvFile.createFrom(mappingFile).stream();
	}

	private void addCommunityRelation(final Row row) {
		String communityId = row.get("communityid");
		String zoneId = row.get("zoneid");
		Optional<DemandZone> zone = zoneRepository.zoneByExternalId(zoneId);
		CommunityZones community = getCommunity(communityId);
		zone.ifPresent(community.zones::add);
	}

	private CommunityZones getCommunity(final String communityId) {
		if (communityZones.containsKey(communityId)) {
			return communityZones.get(communityId);
		}
		CommunityZones community = new CommunityZones(communityId);
		communityZones.put(communityId, community);
		return community;
	}

	private static final class CommunityZones {

		private final List<DemandZone> zones;
		private final String id;

		public CommunityZones(final String id) {
			super();
			this.id = id;
			zones = new LinkedList<>();
		}

		public Community build() {
			return new MultipleZones(id, zones);
		}
	}

}
