package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem.Type;

public class AgeDistributionBuilder {

	private final StructuralData structuralData;

	public AgeDistributionBuilder(StructuralData structuralData) {
		super();
		this.structuralData = structuralData;
	}

	public <T extends AgeDistributionIfc> T buildFor(
			String genderPrefix, Supplier<T> ageDistributionFactory) {
		T ageDistribution = ageDistributionFactory.get();
		List<String> ages = new ArrayList<>();
		for (String attribute : structuralData.getAttributes()) {
			if (attribute.startsWith(genderPrefix)) {
				ages.add(attribute);
			}
		}
		for (int index = 0; index < ages.size() - 1; index++) {
			String maleAge = ages.get(index);
			AgeDistributionItem ageItem = distributionItemFrom(maleAge, AgeDistributionItem.Type.UNTIL);
			ageDistribution.addItem(ageItem);
		}
		String maleAge = ages.get(ages.size() - 1);
		AgeDistributionItem ageItem = distributionItemFrom(maleAge, AgeDistributionItem.Type.OVER);
		ageDistribution.addItem(ageItem);
		return ageDistribution;
	}

	private AgeDistributionItem distributionItemFrom(String ageAsString, Type type) {
		String tmp = ageAsString.replaceFirst("Age:[FM]:", "");
		String[] parts = tmp.split("-");
		int age = (parts.length == 2) ? Integer.parseInt(parts[1]) : Integer.parseInt(parts[0]);
		int number = structuralData.valueOrDefault(ageAsString);
		return new AgeDistributionItem(type, age, number);
	}

}
