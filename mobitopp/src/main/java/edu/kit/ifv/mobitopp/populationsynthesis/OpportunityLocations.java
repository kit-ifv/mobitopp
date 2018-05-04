package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.simulation.opportunities.Opportunity;

public class OpportunityLocations {

	private final List<Opportunity> opportunities;

	public OpportunityLocations() {
		super();
		this.opportunities = new ArrayList<>();
	}

	public void add(Opportunity opportunity) {
		opportunities.add(opportunity);
	}

	public void forEach(Consumer<Opportunity> consumer) {
		opportunities.forEach(consumer);
	}

}
