package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;
import edu.kit.ifv.mobitopp.result.Logger;

public class PrintMaleAge {

	private final DemandZoneRepository zoneRepository;
	private final Logger logger;

	public PrintMaleAge(DemandZoneRepository zoneRepository, Logger logger) {
		super();
		this.zoneRepository = zoneRepository;
		this.logger = logger;
	}

	public void print(Function<DemandZone, Demography> toDemography, String type) {
		MaleAgeDistribution total = new MaleAgeDistribution();
		for (DemandZone zone : getZones()) {
			MaleAgeDistribution distribution = toDemography.apply(zone).maleAge();
			print(distribution);
			increment(total, distribution);
		}
		logger().println("\n" + type + " male age distribution:");
		print(total);
	}

	private List<DemandZone> getZones() {
		return zoneRepository.getZones();
	}

	void increment(MaleAgeDistribution total, MaleAgeDistribution distribution) {
		for (ContinuousDistributionItem item : distribution.getItems()) {
			if (total.hasItem(item.lowerBound())) {
				for (int i = 0; i < item.amount(); i++) {
					total.getItem(item.lowerBound()).increment();
				}
			} else {
				total.addItem(new ContinuousDistributionItem(item));
			}
		}
	}

	void print(MaleAgeDistribution total) {
		int grand_total = 0;
		for (ContinuousDistributionItem item : total.getItems()) {
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
