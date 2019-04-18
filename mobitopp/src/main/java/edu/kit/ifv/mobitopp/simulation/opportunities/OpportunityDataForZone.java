package edu.kit.ifv.mobitopp.simulation.opportunities;

import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.LinkedHashMap;
import java.util.EnumSet;

public class OpportunityDataForZone implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Zone zone;
	private final Attractivities attractivities;
	private final Map<ActivityType, Integer> opportunities;
	private Map<ActivityType, Map<Location, Integer>> opportunityLocations = null;

	public OpportunityDataForZone(Zone zone, Attractivities attractivities) {
		super();
		this.zone = zone;
		this.attractivities = attractivities;
		this.opportunities = createOpportunities(attractivities);
	}
	
	public Map<ActivityType, Integer> opportunities() {
		return Collections.unmodifiableMap(opportunities);
	}

	public Attractivities attractivities() {
		return attractivities;
	}

	public Map<ActivityType, Map<Location, Integer>> opportunityLocations() {
		return Collections.unmodifiableMap(opportunityLocations);
	}

	private Map<ActivityType,Integer> createOpportunities(Attractivities attractivity) {
  	Map<ActivityType,Integer> activityAttractivities = attractivity.getItems();
		Map<ActivityType,Integer> opportunities = new LinkedHashMap<ActivityType,Integer>();
		for (ActivityType activityType :  EnumSet.allOf(ActivityType.class)) {
			if (activityAttractivities.containsKey(activityType)) {
				Integer value = activityAttractivities.get(activityType); 
				opportunities.put(activityType, value.intValue());
			} else {
				opportunities.put(activityType, 0);
			}
		}

		return opportunities;
	}

	public boolean locationsAvailable() {
		return this.opportunityLocations != null;
	}

	public boolean locationsAvailable(ActivityType activityType) {
		return this.opportunityLocations != null 
				&& this.opportunityLocations.containsKey(activityType)
				&& this.opportunityLocations.get(activityType).size() > 0
				;
	}

	public void createLocations(OpportunityLocationSelector locationSelector) {
		this.opportunityLocations = new LinkedHashMap<ActivityType,Map<Location,Integer>>();
		for (ActivityType activityType : this.opportunities.keySet()) {
			Integer total_opportunities =  this.opportunities.get(activityType);
			Map<Location,Integer> locations = new LinkedHashMap<Location,Integer>(
					locationSelector.createLocations(this.zone, activityType, total_opportunities)
			);
			this.opportunityLocations.put(activityType, locations);
		}
	}

	public void forEach(Consumer<Opportunity> consumer) {
		if (locationsAvailable()) {
			forEachInternal(consumer);
		}
	}

	private void forEachInternal(Consumer<Opportunity> consumer) {
		for (Entry<ActivityType, Map<Location, Integer>> entry : opportunityLocations.entrySet()) {
			ActivityType activityType = entry.getKey();
			for (Entry<Location, Integer> opportunity : entry.getValue().entrySet()) {
				consumer.accept(opportunityOf(activityType, opportunity));
			}
		}
	}

	private Opportunity opportunityOf(ActivityType activityType, Entry<Location, Integer> entry) {
		return new Opportunity(zone, activityType, entry.getKey(), entry.getValue());
	}

	public String forLogging() {
		StringBuffer buffer = new StringBuffer();
		for (ActivityType aType : this.opportunities.keySet()) {
			Integer total_size = opportunities.get(aType);
			Map<Location,Integer> locations = this.opportunityLocations.get(aType);
			buffer.append("\nZZ-TOTAL; ");
			buffer.append(zone.getInternalId().getExternalId() + "; ");
			buffer.append(zone.getInternalId().getMatrixColumn() + "; ");
			buffer.append(aType.getTypeAsInt() + "; ");
			buffer.append(total_size + "; ");
			buffer.append(locations.size() + "; ");
			for (Location location : locations.keySet()) {
				Integer size = locations.get(location);
				buffer.append("\nZZ; ");
				buffer.append(zone.getInternalId().getMatrixColumn() + "; ");
				buffer.append(aType.getTypeAsInt() + "; ");
				buffer.append(size + "; ");
				buffer.append(location.forLogging() + "; ");
			}
		}
		return buffer.toString();
	}

	public String toString() {
		return forLogging();
	}

	public Location selectRandomLocation(ActivityType activityType, double rand) {
		assert locationsAvailable(activityType) : (activityType + " " + this);
		Map<Location,Integer> locations = this.opportunityLocations.get(activityType);
		assert locations.size() > 0 : activityType;
		DiscreteRandomVariable<Location> locationDistribution = new DiscreteRandomVariable<Location>(locations);
		return locationDistribution.realization(rand);
	}

}
