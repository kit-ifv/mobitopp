package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;

import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.populationsynthesis.community.CommunityRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.community.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class CommunityDemandCalculator implements DemandDataCalculator {

	private final DemandDataForDemandRegionCalculator calculator;
	private final List<PopulationSynthesisStep> steps;
	private final ImpedanceIfc impedance;
	private final CommunityRepository communities;

	public CommunityDemandCalculator(
			CommunityRepository communities, DemandDataForDemandRegionCalculator calculator,
			List<PopulationSynthesisStep> steps, ImpedanceIfc impedance) {
		this.communities = communities;
		this.calculator = calculator;
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
		calculator.calculateDemandData(community, impedance);
	}

	private void processSteps(Community community) {
		steps.forEach(s -> s.process(community));
	}

}
