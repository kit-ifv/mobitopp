package edu.kit.ifv.mobitopp.populationsynthesis.region;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataForDemandRegionCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataForZoneCalculatorIfc;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class IsolatedZonesDemandCalculator implements DemandDataForDemandRegionCalculator {

	private final DemandDataForZoneCalculatorIfc zoneCalculator;

	public IsolatedZonesDemandCalculator(DemandDataForZoneCalculatorIfc zoneCalculator) {
		this.zoneCalculator = zoneCalculator;
	}

	@Override
	public void calculateDemandData(DemandRegion community, ImpedanceIfc impedance) {
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
