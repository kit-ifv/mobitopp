package edu.kit.ifv.mobitopp.populationsynthesis.community;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataForCommunityCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataForZoneCalculatorIfc;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class IsolatedZonesCommunityDemandCalculator implements DemandDataForCommunityCalculator {

	private final DemandDataForZoneCalculatorIfc zoneCalculator;

	public IsolatedZonesCommunityDemandCalculator(DemandDataForZoneCalculatorIfc zoneCalculator) {
		this.zoneCalculator = zoneCalculator;
	}

	@Override
	public void calculateDemandData(Community community, ImpedanceIfc impedance) {
		community
				.zones()
				.filter(this::zonesToGenerate)
				.forEach(z -> zoneCalculator.calculateDemandData(z, impedance));
	}

	private boolean zonesToGenerate(DemandZone zone) {
		return zone.shouldGeneratePopulation()
				&& !ZoneClassificationType.outlyingArea.equals(zone.zone().getClassification());
	}

}
