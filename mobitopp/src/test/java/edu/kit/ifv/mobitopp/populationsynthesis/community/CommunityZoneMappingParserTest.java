package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

@ExtendWith(MockitoExtension.class)
public class CommunityZoneMappingParserTest {

	private static final String communityId = "1";

	@Mock
	private DemandZoneRepository zoneRepository;
	@Mock
	private DemographyRepository demographyRepository;

	private Demography communityDemography;

	@BeforeEach
	private void initialise() {
		communityDemography = new Demography(EmploymentDistribution.createDefault(), emptyMap());
		when(demographyRepository.getDemographyFor(community, communityId))
				.thenReturn(communityDemography);
	}

	@Test
	void parseCommunityRelations() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		setAvailable(someZone);
		Row row = createMappingFor(someZone);
		Map<String, Community> repository = parse(row);

		assertThat(repository,
				hasEntry(communityId, new MultipleZones(communityId, communityDemography, someZone)));
		verify(demographyRepository).getDemographyFor(community, communityId);
	}

	@Test
	void parseMissingZone() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		Row row = createMappingFor(someZone);
		Map<String, Community> repository = parse(row);

		assertThat(repository,
				hasEntry(communityId, new MultipleZones(communityId, communityDemography)));
		verify(demographyRepository).getDemographyFor(community, communityId);
	}

	private Row createMappingFor(DemandZone someZone) {
		return Row
				.createRow(asList(communityId, someZone.getId().getExternalId()),
						asList("communityId", "zoneId"));
	}

	private void setAvailable(DemandZone someZone) {
		when(zoneRepository.zoneByExternalId(someZone.getId().getExternalId()))
				.thenReturn(Optional.of(someZone));
	}

	private Map<String, Community> parse(Row row) {
		File mappingFile = new File("dummy");
		return new CommunityZoneMappingParser(zoneRepository, demographyRepository) {

			Stream<Row> load(File mappingFile) {
				return Stream.of(row);
			};

		}.parse(mappingFile);
	}
}
