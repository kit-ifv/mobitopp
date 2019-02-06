package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;

public class HouseholdDistributionBuilder {

	private final StructuralData structuralData;
  private final AttributeType attributeType;

	public HouseholdDistributionBuilder(StructuralData structuralData, AttributeType attributeType) {
		super();
		this.structuralData = structuralData;
    this.attributeType = attributeType;
	}

	public RangeDistributionIfc build(String zoneId) {
		RangeDistributionIfc distribution = new RangeDistribution();
		for (String type : householdTypes()) {
			int amount = structuralData.valueOrDefault(zoneId, type);
			int code = Integer.parseInt(type.replaceAll(attributeType.prefix(), ""));
			RangeDistributionItem hhItem = new RangeDistributionItem(code, amount);
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
