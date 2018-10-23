package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.result.Logger;

public class PrintFemaleAge {

	private final DemandZoneRepository zoneRepository;
	private final Logger logger;

	public PrintFemaleAge(DemandZoneRepository zoneRepository, Logger logger) {
		super();
		this.zoneRepository = zoneRepository;
		this.logger = logger;
	}

	public void print(Function<DemandZone, Demography> toDemography, String type) {
		FemaleAgeDistribution total = new FemaleAgeDistribution();
		for (DemandZone zone : getZones()) {
			FemaleAgeDistribution distribution = toDemography.apply(zone).femaleAge();
			print(distribution);
			increment(total, distribution);
		}
		logger().println("\n" + type + " female age distribution:");
		print(total);
	}

	private List<DemandZone> getZones() {
		return zoneRepository.getZones();
	}

	void increment(FemaleAgeDistribution total, FemaleAgeDistribution distribution) {
		for (AgeDistributionItem item : distribution.getItems()) {
			if (total.hasItem(item.lowerBound())) {
				for (int i = 0; i < item.amount(); i++) {
					total.getItem(item.lowerBound()).increment();
				}
			} else {
				total.addItem(new AgeDistributionItem(item));
			}
		}
	}

	void print(FemaleAgeDistribution total) {
		int grand_total = 0;
		for (AgeDistributionItem item : total.getItems()) {
			logger().println(item.lowerBound() + "-" + item.upperBound() + ": " + item.amount());
			grand_total += item.amount();
		}
		logger().println("TOTAL\t" + grand_total);
		logger().println("\n\n");
	}

	Logger logger() {
		return logger;
	}

}
