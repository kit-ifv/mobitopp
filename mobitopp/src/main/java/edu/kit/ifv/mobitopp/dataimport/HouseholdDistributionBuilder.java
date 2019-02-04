package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;

public class HouseholdDistributionBuilder {

	private static final String householdPrefix = "hhtyp:";

	private final StructuralData structuralData;

	public HouseholdDistributionBuilder(StructuralData structuralData) {
		super();
		this.structuralData = structuralData;
	}

	public HouseholdDistribution build(String zoneId) {
		HouseholdDistribution distribution = new HouseholdDistribution();
		for (String type : householdTypes()) {
			int amount = structuralData.valueOrDefault(zoneId, type);
			int code = Integer.parseInt(type.replaceAll(householdPrefix, ""));
			HouseholdDistributionItem hhItem = new HouseholdDistributionItem(code, amount);
			distribution.addItem(hhItem);
		}
		return distribution;
	}

	private List<String> householdTypes() {
		List<String> households = new ArrayList<>();
		for (String attribute : structuralData.getAttributes()) {
			if (attribute.startsWith(householdPrefix)) {
				households.add(attribute);
			}
		}
		return households;
	}

}
