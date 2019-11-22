package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PopulationForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;

public class HouseholdBasedStep implements PopulationSynthesisStep {

	private final Consumer<HouseholdForSetup> consumer;

	public HouseholdBasedStep(final Consumer<HouseholdForSetup> consumer) {
		super();
		this.consumer = consumer;
	}

	@Override
	public void process(final Community community) {
		community
				.zones()
				.map(DemandZone::getPopulation)
				.flatMap(PopulationForSetup::households)
				.forEach(consumer);
	}

}
