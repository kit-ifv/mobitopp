package edu.kit.ifv.mobitopp.populationsynthesis.community;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataForZoneCalculatorIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class IsolatedZonesCommunityDemandCalculator implements DemandDataCalculator {

	private final DemandDataForZoneCalculatorIfc zoneCalculator;
	private final List<PopulationSynthesisStep> steps;
	private final ImpedanceIfc impedance;
	private final CommunityRepository communities;

	public IsolatedZonesCommunityDemandCalculator(
			CommunityRepository communities, DemandDataForZoneCalculatorIfc zoneCalculator,
			List<PopulationSynthesisStep> steps, ImpedanceIfc impedance) {
		this.communities = communities;
		this.zoneCalculator = zoneCalculator;
		this.steps = steps;
		this.impedance = impedance;
	}

	@Override
	public void calculateDemand() {
		for (Community community : communities.getCommunities()) {
			createHouseholds(community);
			processSteps(community);
		}
	}

	private void createHouseholds(Community community) {
		community
				.zones()
				.filter(this::zonesToGenerate)
				.forEach(z -> zoneCalculator.calculateDemandData(z, impedance));
	}

	private boolean zonesToGenerate(DemandZone zone) {
		return zone.shouldGeneratePopulation()
				&& !ZoneClassificationType.outlyingArea.equals(zone.zone().getClassification());
	}

	private void processSteps(Community community) {
		steps.forEach(s -> s.process(community));
	}

}
