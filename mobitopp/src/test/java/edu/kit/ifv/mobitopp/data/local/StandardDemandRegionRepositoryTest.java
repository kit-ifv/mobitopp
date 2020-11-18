package edu.kit.ifv.mobitopp.data.local;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultRegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;

@ExtendWith(MockitoExtension.class)
public class StandardDemandRegionRepositoryTest {

	@Mock
	private DemandRegion someRegion;
	@Mock
	private DemandRegion someZone;

	@Test
	void findRegionForHighestLevel() throws Exception {
		RegionalContext id = new DefaultRegionalContext(RegionalLevel.community, "1");
		Map<RegionalContext, DemandRegion> mapping = Map.of(id, someRegion);
		StandardDemandRegionRepository repository = new StandardDemandRegionRepository(mapping);
		
		assertThat(repository.getRegionWith(id)).hasValue(someRegion);
	}
	
	@Test
	void findRegionForLowerLevel() throws Exception {
		RegionalContext communityId = new DefaultRegionalContext(RegionalLevel.community, "1");
		RegionalContext zoneId = new DefaultRegionalContext(RegionalLevel.zone, "1");
		Map<RegionalContext, DemandRegion> mapping = Map.of(communityId, someRegion, zoneId, someZone);
		StandardDemandRegionRepository repository = new StandardDemandRegionRepository(mapping);
		
		assertThat(repository.getRegionWith(communityId)).hasValue(someRegion);
	}
	
	@Test
	void getRegionsForSpecificLevel() throws Exception {
		RegionalContext communityId = new DefaultRegionalContext(RegionalLevel.community, "1");
		RegionalContext zoneId = new DefaultRegionalContext(RegionalLevel.zone, "1");
		Map<RegionalContext, DemandRegion> mapping = Map.of(communityId, someRegion, zoneId, someZone);
		StandardDemandRegionRepository repository = new StandardDemandRegionRepository(mapping);
		
		List<DemandRegion> communities = repository.getRegionsOf(RegionalLevel.community);
		List<DemandRegion> zones = repository.getRegionsOf(RegionalLevel.zone);
		
		assertThat(communities).containsExactly(someRegion);
		assertThat(zones).containsExactly(someZone);
	}
	
}
