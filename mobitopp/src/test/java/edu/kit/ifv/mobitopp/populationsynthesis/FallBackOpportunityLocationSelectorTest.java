package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;

@ExtendWith(MockitoExtension.class)
public class FallBackOpportunityLocationSelectorTest {

	@Mock
	private OpportunityLocationSelector selector;
	@Mock
	private OpportunityLocationSelector defaultSelector;

	@Test
	void fallBackIfLocationsAreMissing() throws Exception {
		ZoneId zone = new ZoneId("1", 1);
		ActivityType activityType = ActivityType.WORK;
		Integer total_opportunities = 1;
		when(selector.createLocations(zone, activityType, total_opportunities)).thenReturn(Map.of());
		InOrder inOrder = inOrder(selector, defaultSelector);

		FallBackOpportunityLocationSelector fallBack = new FallBackOpportunityLocationSelector(selector,
				defaultSelector);

		fallBack.createLocations(zone, activityType, total_opportunities);

		inOrder.verify(selector).createLocations(zone, activityType, total_opportunities);
		inOrder.verify(defaultSelector).createLocations(zone, activityType, total_opportunities);
	}
}
