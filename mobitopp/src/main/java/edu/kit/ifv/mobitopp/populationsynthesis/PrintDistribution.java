package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;

public class PrintDistribution {

	private final DemandZoneRepository zoneRepository;
	private final PrintEmployment employment;
	private final PrintMaleAge maleAge;
	private final PrintFemaleAge femaleAge;

	public PrintDistribution(DemandZoneRepository zoneRepository) {
		super();
		this.zoneRepository = zoneRepository;
		employment = new PrintEmployment(zoneRepository, System.out::println);
		maleAge = new PrintMaleAge(zoneRepository, System.out::println);
		femaleAge = new PrintFemaleAge(zoneRepository, System.out::println);
	}

	protected List<DemandZone> getZones() {
		return zoneRepository.getZones();
	}

	void printActualDistributions() {
		print(DemandZone::actualDemography, "Actual");
	}

	void printNominalDistributions() {
		print(DemandZone::nominalDemography, "Nominal");
	}

	private void print(Function<DemandZone, Demography> toDemography, String type) {
		employment.print(toDemography, type);
		femaleAge.print(toDemography, type);
		maleAge.print(toDemography, type);
	}

}