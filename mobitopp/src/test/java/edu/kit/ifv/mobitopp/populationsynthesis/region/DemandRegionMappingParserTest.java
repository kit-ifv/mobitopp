package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
import edu.kit.ifv.mobitopp.populationsynthesis.community.DemographyRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultRegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

@ExtendWith(MockitoExtension.class)
public class DemandRegionMappingParserTest {

	private static final String regionId = "1";
	private static final RegionalContext regionContext = new DefaultRegionalContext(community, regionId);

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
		Map<RegionalContext, DemandRegion> repository = parse(community, row);

		assertThat(repository,
				hasEntry(regionContext, new MultipleRegions(regionId, community, regionDemography, somePart)));
		verify(demographyRepository).getDemographyFor(community, regionId);
	}

	private void setAvailable(DemandRegion somePart) {
		when(partRepository.apply(somePart.getExternalId())).thenReturn(Optional.of(somePart));
	}

	@Test
	void parseMissingPart() throws Exception {
		DemandZone somePart = ExampleDemandZones.create().getSomeZone();
		setPartUnavailable(somePart);
		Row row = createMappingFor(somePart);
		Map<RegionalContext, DemandRegion> repository = parse(community, row);

		assertThat(repository)
				.containsEntry(regionContext, new MultipleRegions(regionId, community, regionDemography));
		verify(demographyRepository).getDemographyFor(community, regionId);
	}

	private void setPartUnavailable(DemandZone somePart) {
		when(partRepository.apply(somePart.getExternalId())).thenReturn(Optional.empty());
	}

	private Row createMappingFor(DemandRegion somePart) {
		return Row
				.createRow(List.of(regionId, somePart.getExternalId()),
						asList(DemandRegionMappingParser.regionColumn, DemandRegionMappingParser.partColumn));
	}

	private Map<RegionalContext, DemandRegion> parse(RegionalLevel regionalLevel, Row row) {
		return new DemandRegionMappingParser(regionalLevel, partRepository, demographyRepository)
				.parse(Stream.of(row));
	}
}
