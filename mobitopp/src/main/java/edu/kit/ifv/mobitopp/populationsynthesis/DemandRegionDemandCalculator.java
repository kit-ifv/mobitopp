package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.region.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class DemandRegionDemandCalculator implements DemandDataCalculator {

	private final DemandDataForDemandRegionCalculator calculator;
	private final List<PopulationSynthesisStep> steps;
	private final ImpedanceIfc impedance;
	private final List<DemandRegion> regions;

	public DemandRegionDemandCalculator(
			final List<DemandRegion> regions, final DemandDataForDemandRegionCalculator calculator,
			final List<PopulationSynthesisStep> steps, final ImpedanceIfc impedance) {
		this.regions = regions;
		this.calculator = calculator;
		this.steps = steps;
		this.impedance = impedance;
	}

	@Override
	public void calculateDemand() {
		for (DemandRegion region : regions) {
			createHouseholds(region);
			processSteps(region);
		}
	}

	private void createHouseholds(DemandRegion region) {
		calculator.calculateDemandData(region, impedance);
	}

	private void processSteps(DemandRegion region) {
		steps.forEach(s -> s.process(region));
	}

}
