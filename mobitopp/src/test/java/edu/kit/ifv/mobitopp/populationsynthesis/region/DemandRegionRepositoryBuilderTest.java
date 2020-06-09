package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionMappingParser.partColumn;
import static edu.kit.ifv.mobitopp.populationsynthesis.region.DemandRegionMappingParser.regionColumn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.DemographyRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.local.DemandRegionMapping;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandRegionRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultRegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import edu.kit.ifv.mobitopp.util.dataimport.Row;

@ExtendWith(MockitoExtension.class)
public class DemandRegionRepositoryBuilderTest {

	private static final String communityId = "1";
	private static final String districtId = "2";
	private static final String zoneId = "3";

	@Mock
	private DemandRegionMapping demandRegionMapping;
	@Mock
	private DemandZoneRepository demandZoneRepository;
	@Mock
	private DemographyRepository demographyRepository;
	@Mock
	private DemandZone someZone;
	@Mock
	private Demography someDemography;

	@Test
	void buildsUpTwoLevelsRepository() throws Exception {
		SortedMap<RegionalLevel, RegionalLevel> levels = new TreeMap<>();
		levels.put(RegionalLevel.zone, RegionalLevel.district);
		levels.put(RegionalLevel.district, RegionalLevel.community);
		when(demandRegionMapping.getLowToHigh()).thenReturn(levels);
		when(demandRegionMapping.contentFor(RegionalLevel.zone)).then(districtToZone());
		when(demandRegionMapping.contentFor(RegionalLevel.district)).then(communityToDistrict());
		when(demographyRepository.getDemographyFor(RegionalLevel.district, districtId))
				.thenReturn(someDemography);
		when(demographyRepository.getDemographyFor(RegionalLevel.community, communityId))
				.thenReturn(someDemography);
		when(someZone.getRegionalContext()).thenReturn(zoneContext());
		when(demandZoneRepository.zoneByExternalId(zoneId)).thenReturn(Optional.of(someZone));
		when(demandZoneRepository.getZones()).thenReturn(List.of(someZone));
		DemandRegionRepositoryBuilder builder = new DemandRegionRepositoryBuilder(demandRegionMapping,
				demandZoneRepository, demographyRepository);

		DemandRegionRepository repository = builder.create();

		DemandRegion community = repository.getRegionWith(RegionalLevel.community, communityId);
		DemandRegion district = repository.getRegionWith(RegionalLevel.district, districtId);
		DemandRegion zone = repository.getRegionWith(RegionalLevel.zone, zoneId);
		assertThat(community.getRegionalContext()).isEqualTo(communityContext());
		assertThat(community.parts())
				.anyMatch(part -> districtContext().equals(part.getRegionalContext()));
		assertThat(district.getRegionalContext()).isEqualTo(districtContext());
		assertThat(district.parts()).contains(someZone);
		assertThat(zone).isEqualTo(someZone);
	}
	
	@Test
	void buildsUpRepositoryWithoutMapping() throws Exception {
		when(demandRegionMapping.getLowToHigh()).thenReturn(new TreeMap<>());
		when(someZone.getRegionalContext()).thenReturn(zoneContext());
		when(demandZoneRepository.getZones()).thenReturn(List.of(someZone));
		DemandRegionRepositoryBuilder builder = new DemandRegionRepositoryBuilder(demandRegionMapping,
				demandZoneRepository, demographyRepository);

		DemandRegionRepository repository = builder.create();

		DemandRegion community = repository.getRegionWith(RegionalLevel.community, communityId);
		DemandRegion zone = repository.getRegionWith(RegionalLevel.zone, zoneId);
		assertThat(community).isNull();
		assertThat(zone).isEqualTo(someZone);
		verify(demandRegionMapping, never()).contentFor(any());
	}

	private RegionalContext zoneContext() {
		return contextOf(RegionalLevel.zone, zoneId);
	}

	private DefaultRegionalContext contextOf(RegionalLevel level, String id) {
		return new DefaultRegionalContext(level, id);
	}

	private RegionalContext districtContext() {
		return contextOf(RegionalLevel.district, districtId);
	}

	private RegionalContext communityContext() {
		return contextOf(RegionalLevel.community, communityId);
	}

	private Answer<?> districtToZone() {
		return stream(List.of(districtId, zoneId));
	}

	private Answer<?> communityToDistrict() {
		return stream(List.of(communityId, districtId));
	}

	private Answer<?> stream(List<String> values) {
		List<String> attributes = List.of(regionColumn, partColumn);
		Row row = Row.createRow(values, attributes);
		return (invocation) -> Stream.of(row);
	}
}
