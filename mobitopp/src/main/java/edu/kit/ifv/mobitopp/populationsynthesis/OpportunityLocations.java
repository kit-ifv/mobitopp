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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((opportunities == null) ? 0 : opportunities.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OpportunityLocations other = (OpportunityLocations) obj;
		if (opportunities == null) {
			if (other.opportunities != null)
				return false;
		} else if (!opportunities.equals(other.opportunities))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OpportunityLocations [opportunities=" + opportunities + "]";
	}

}
