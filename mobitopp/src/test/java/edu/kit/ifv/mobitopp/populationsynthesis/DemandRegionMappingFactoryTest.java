package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.district;
import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.zone;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.SortedMap;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.local.DemandRegionMapping;

public class DemandRegionMappingFactoryTest {

	@Test
	void buildsUpMapping() throws Exception {
		String communityToDistrict = buildIdentifier(community, district);
		String districtToZone = buildIdentifier(district, zone);
		String somePath = "some-path";
		Map<String, String> configuration = Map
				.of(communityToDistrict, somePath, districtToZone, somePath);

		DemandRegionMapping mapping = new DemandRegionMappingFactory().create(configuration);
		SortedMap<RegionalLevel, RegionalLevel> lowToHigh = mapping.getLowToHigh();

		assertThat(lowToHigh)
				.containsExactlyInAnyOrderEntriesOf(Map.of(zone, district, district, community));
	}

	private String buildIdentifier(RegionalLevel region, RegionalLevel part) {
		return region.identifier() + ":" + part.identifier();
	}
}
