package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import java.util.Random;

import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.simulation.Location;

public abstract class RandomHouseholdLocationSelector implements HouseholdLocationSelector {

	protected final SimpleRoadNetwork network;
	private final Random random;

	public RandomHouseholdLocationSelector(SynthesisContext context) {
		this.network = context.roadNetwork();
		this.random = new Random(context.seed());
	}

	public Location selectLocation(edu.kit.ifv.mobitopp.data.Zone dataZone) {
		Zone zone = this.network.zone(dataZone.getId());
		return selectLocation(zone);
	}

	protected Random random() {
		return random;
	}

}
