package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.result.Logger;

public class PrintDistribution {

	private final DemandZoneRepository zoneRepository;

	public PrintDistribution(DemandZoneRepository zoneRepository) {
		super();
		this.zoneRepository = zoneRepository;
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
		EmploymentDistribution total = new EmploymentDistribution();
		for (DemandZone zone : getZones()) {
			EmploymentDistribution distribution = toDemography.apply(zone).employment();
			increment(total, distribution);
		}
		logger().println("\n" + type + " distribution:");
		print(total);
	}

	void increment(EmploymentDistribution total, EmploymentDistribution distribution) {
		for (EmploymentDistributionItem item : distribution.getItems()) {
			for (int i = 0; i < item.amount(); i++) {
				total.getItem(item.type()).increment();
			}
		}
	}

	void print(EmploymentDistribution total) {
		int grand_total = 0;
		for (EmploymentDistributionItem item : total.getItems()) {
			logger().println(item.type().getTypeAsString() + ": " + item.amount());
			grand_total += item.amount();
		}
		logger().println("TOTAL\t" + grand_total);
		logger().println("\n\n");
	}

	Logger logger() {
		return System.out::println;
	}
}