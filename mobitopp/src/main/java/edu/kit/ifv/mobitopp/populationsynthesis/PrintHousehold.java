package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.result.Logger;

public class PrintHousehold {

	private final DemandZoneRepository zoneRepository;
	private final Logger logger;

	public PrintHousehold(DemandZoneRepository zoneRepository, Logger logger) {
		super();
		this.zoneRepository = zoneRepository;
		this.logger = logger;
	}

	public void print(Function<DemandZone, Demography> toDemography, String type) {
		HouseholdDistribution total = new HouseholdDistribution();
		for (DemandZone zone : getZones()) {
			HouseholdDistribution distribution = toDemography.apply(zone).household();
			print(distribution);
			increment(total, distribution);
		}
		logger().println("\n" + type + " employment distribution:");
		print(total);
	}

	private List<DemandZone> getZones() {
		return zoneRepository.getZones();
	}

	void increment(HouseholdDistribution total, HouseholdDistribution distribution) {
		for (HouseholdDistributionItem item : distribution.getItems()) {
			for (int i = 0; i < item.amount(); i++) {
				total.getItem(item.type()).increment();
			}
		}
	}

	void print(HouseholdDistribution distribution) {
		int grand_total = 0;
		for (HouseholdDistributionItem item : distribution.getItems()) {
			logger().println(item.type() + ": " + item.amount());
			grand_total += item.amount();
		}
		logger().println("TOTAL\t" + grand_total);
		logger().println("\n\n");
	}

	Logger logger() {
		return logger;
	}

}
