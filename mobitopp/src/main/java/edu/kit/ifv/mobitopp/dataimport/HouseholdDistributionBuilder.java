package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public class HouseholdDistributionBuilder {

	private final StructuralData structuralData;
  private final AttributeType attributeType;

	public HouseholdDistributionBuilder(StructuralData structuralData, AttributeType attributeType) {
		super();
		this.structuralData = structuralData;
    this.attributeType = attributeType;
	}

	public HouseholdDistribution build(String zoneId) {
		HouseholdDistribution distribution = new HouseholdDistribution();
		for (String type : householdTypes()) {
			int amount = structuralData.valueOrDefault(zoneId, type);
			int code = Integer.parseInt(type.replaceAll(attributeType.prefix(), ""));
			HouseholdDistributionItem hhItem = new HouseholdDistributionItem(code, amount);
			distribution.addItem(hhItem);
		}
		return distribution;
	}

	private List<String> householdTypes() {
		List<String> households = new ArrayList<>();
		for (String attribute : structuralData.getAttributes()) {
			if (attribute.startsWith(attributeType.attributeName())) {
				households.add(attribute);
			}
		}
		return households;
	}

}
