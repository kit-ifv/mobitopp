package edu.kit.ifv.mobitopp.populationsynthesis.calculator;

import java.time.LocalDateTime;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataForZoneCalculatorIfc;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class SingleZoneDemandCalculator implements DemandDataForCommunityCalculator {

	private final DemandDataForZoneCalculatorIfc calculator;
	private final DemandZoneRepository demandZoneRepository;
	private final ImpedanceIfc impedance;

	public SingleZoneDemandCalculator(
			DemandDataForZoneCalculatorIfc calculator, DemandZoneRepository demandZoneRepository,
			ImpedanceIfc impedance) {
		super();
		this.calculator = calculator;
		this.demandZoneRepository = demandZoneRepository;
		this.impedance = impedance;
	}

	@Override
	public void calculateDemand() {
		for (DemandZone zone : demandZoneRepository.getZones()) {
      System.out
          .println(
              String.format("%s: PopulationSynthesis: Zone %s", LocalDateTime.now(), zone.getId()));
      calculator.calculateDemandData(zone, impedance);
    }
	}

}
