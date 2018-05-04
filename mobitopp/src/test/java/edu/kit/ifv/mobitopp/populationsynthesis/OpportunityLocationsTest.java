package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class OpportunityLocationsTest {

	private Opportunity aOpportunity;
	private Opportunity otherOpportunity;
	private Consumer<Opportunity> consumer;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		aOpportunity = mock(Opportunity.class);
		otherOpportunity = mock(Opportunity.class);
		consumer = mock(Consumer.class);
	}
	
	@Test
	public void processEachOpportunity() {
		OpportunityLocations opportunities = new OpportunityLocations();

		opportunities.add(aOpportunity);
		opportunities.add(otherOpportunity);
		
		opportunities.forEach(consumer);
		
		verify(consumer).accept(aOpportunity);
		verify(consumer).accept(otherOpportunity);
	}
}
