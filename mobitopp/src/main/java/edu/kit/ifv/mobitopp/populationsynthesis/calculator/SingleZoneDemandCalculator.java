package edu.kit.ifv.mobitopp.populationsynthesis.calculator;

import java.time.LocalDateTime;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DemandDataForZoneCalculatorIfc;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class SingleZoneDemandCalculator implements DemandDataCalculator {

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
		demandZoneRepository
				.getZones()
				.stream()
				.filter(DemandZone::shouldGeneratePopulation)
				.peek(zone -> System.out
						.println(String
								.format("%s: PopulationSynthesis: Zone %s", LocalDateTime.now(), zone.getId())))
				.forEach(zone -> calculator.calculateDemandData(zone, impedance));
	}

}
