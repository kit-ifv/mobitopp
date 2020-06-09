package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.DemographyRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultRegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

@ExtendWith(MockitoExtension.class)
public class DemandRegionZoneMappingParserTest {

	private static final String communityId = "1";
	private static final RegionalContext communityContext = new DefaultRegionalContext(community,
			communityId);

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
		Map<RegionalContext, DemandRegion> repository = parse(row);

		assertThat(repository)
				.containsEntry(communityContext,
						new MultipleZones(communityId, community, communityDemography, someZone));
		verify(demographyRepository).getDemographyFor(community, communityId);
	}

	private void setAvailable(DemandZone someZone) {
		when(zoneRepository.zoneByExternalId(someZone.getId().getExternalId()))
		.thenReturn(Optional.of(someZone));
	}

	@Test
	void parseMissingZone() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		setUnavailable(someZone);
		Row row = createMappingFor(someZone);
		Map<RegionalContext, DemandRegion> repository = parse(row);

		assertThat(repository)
				.containsEntry(communityContext,
						new MultipleZones(communityId, community, communityDemography));
		verify(demographyRepository).getDemographyFor(community, communityId);
	}

	private void setUnavailable(DemandZone someZone) {
		when(zoneRepository.zoneByExternalId(someZone.getId().getExternalId()))
		.thenReturn(Optional.of(someZone));
	}

	private Row createMappingFor(DemandZone someZone) {
		return Row
				.createRow(asList(communityId, someZone.getId().getExternalId()),
						asList("regionId", "partId"));
	}

	private Map<RegionalContext, DemandRegion> parse(Row row) {
		return new DemandRegionZoneMappingParser(RegionalLevel.community, zoneRepository,
				demographyRepository).parse(Stream.of(row));
	}
}
