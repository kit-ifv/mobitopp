package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

@ExtendWith(MockitoExtension.class)
public class DemandRegionMappingParserTest {

	private static final String regionId = "1";

	@Mock
	private Function<String, Optional<DemandRegion>> partRepository;
	@Mock
	private DemographyRepository demographyRepository;

	private Demography regionDemography;

	@BeforeEach
	private void initialise() {
		regionDemography = new Demography(EmploymentDistribution.createDefault(), emptyMap());
		when(demographyRepository.getDemographyFor(community, regionId)).thenReturn(regionDemography);
	}

	@Test
	void parseRegionRelations() throws Exception {
		DemandRegion somePart = ExampleDemandZones.create().getSomeZone();
		setAvailable(somePart);
		Row row = createMappingFor(somePart);
		Map<String, DemandRegion> repository = parse(community, row);

		assertThat(repository,
				hasEntry(regionId, new MultipleRegions(regionId, community, regionDemography, somePart)));
		verify(demographyRepository).getDemographyFor(community, regionId);
	}

	@Test
	void parseMissingPart() throws Exception {
		DemandZone somePart = ExampleDemandZones.create().getSomeZone();
		Row row = createMappingFor(somePart);
		Map<String, DemandRegion> repository = parse(community, row);

		assertThat(repository,
				hasEntry(regionId, new MultipleZones(regionId, community, regionDemography)));
		verify(demographyRepository).getDemographyFor(community, regionId);
	}

	private Row createMappingFor(DemandRegion somePart) {
		return Row
				.createRow(asList(regionId, somePart.getExternalId()),
						asList(DemandRegionMappingParser.regionColumn, DemandRegionMappingParser.partColumn));
	}

	private void setAvailable(DemandRegion somePart) {
		when(partRepository.apply(somePart.getExternalId())).thenReturn(Optional.of(somePart));
	}

	private Map<String, DemandRegion> parse(RegionalLevel regionalLevel, Row row) {
		return new DemandRegionMappingParser(regionalLevel, partRepository, demographyRepository)
				.parse(Stream.of(row));
	}
}
