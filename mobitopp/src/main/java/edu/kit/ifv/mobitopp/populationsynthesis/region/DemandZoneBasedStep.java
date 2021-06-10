package edu.kit.ifv.mobitopp.populationsynthesis.region;

import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;

public class DemandZoneBasedStep implements PopulationSynthesisStep {

	private final Consumer<DemandZone> consumer;

	public DemandZoneBasedStep(Consumer<DemandZone> consumer) {
		super();
		this.consumer = consumer;
	}

	@Override
	public void process(final DemandRegion region) {		
		region.zones().forEach(consumer::accept);
	}

}
