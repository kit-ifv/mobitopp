package edu.kit.ifv.mobitopp.populationsynthesis.calculator;

import java.time.LocalDateTime;
import java.util.function.Predicate;

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
		Predicate<DemandZone> zonesWithPopulation = DemandZone::shouldGeneratePopulation;
		Predicate<DemandZone> zonesWithoutPopulation = zonesWithPopulation.negate();
		calculateDemandFor(zonesWithPopulation);
		saveZoneAttributesOf(zonesWithoutPopulation);
	}

	private void calculateDemandFor(Predicate<DemandZone> zonesWithPopulation) {
		demandZoneRepository
				.getZones()
				.stream()
				.filter(zonesWithPopulation)
				.peek(zone -> System.out
						.println(String
								.format("%s: Calculate demand for: Zone %s", LocalDateTime.now(), zone.getId())))
				.forEach(this::calculateAndSave);
	}

	private void calculateAndSave(DemandZone zone) {
		calculator.calculateDemandData(zone, impedance);
		calculator.saveDemandData(zone);
	}

	private void saveZoneAttributesOf(Predicate<DemandZone> zonesWithoutPopulation) {
		demandZoneRepository
				.getZones()
				.stream()
				.filter(zonesWithoutPopulation)
				.peek(zone -> System.out
						.println(String
								.format("%s: PopulationSynthesis: Zone %s", LocalDateTime.now(), zone.getId())))
				.forEach(zone -> calculator.saveDemandData(zone));
	}

}
