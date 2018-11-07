package edu.kit.ifv.mobitopp.simulation.opportunities;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.function.Consumer;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;

public class OpportunityDataForZoneTest {

	@Test
	public void suppressNullPointerException() {
		@SuppressWarnings("unchecked")
		Consumer<Opportunity> consumeOpportunity = mock(Consumer.class);
		Zone someZone = ExampleZones.create().someZone();
		OpportunityDataForZone opportunities = new OpportunityDataForZone(someZone, someZone.attractivities());

		opportunities.forEach(consumeOpportunity);
		
		verifyZeroInteractions(consumeOpportunity);
	}
}
