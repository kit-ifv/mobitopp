package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;

public class AgeDistributionBuilder {

	private static final String malePrefix = "age:m:";
	private static final String femalePrefix = "age:f:";

	private final StructuralData structuralData;

	public AgeDistributionBuilder(StructuralData structuralData) {
		super();
		this.structuralData = structuralData;
	}

	public MaleAgeDistribution buildMale() {
		return buildFor(malePrefix, MaleAgeDistribution::new);
	}

	public FemaleAgeDistribution buildFemale() {
		return buildFor(femalePrefix, FemaleAgeDistribution::new);
	}

	private <T extends AgeDistributionIfc> T buildFor(
			String genderPrefix, Supplier<T> ageDistributionFactory) {
		T ageDistribution = ageDistributionFactory.get();
		List<String> ages = new ArrayList<>();
		for (String attribute : structuralData.getAttributes()) {
			if (attribute.startsWith(genderPrefix)) {
				ages.add(attribute);
			}
		}
		for (int index = 0; index < ages.size(); index++) {
			String age = ages.get(index);
			AgeDistributionItem ageItem = distributionItemFrom(age);
			ageDistribution.addItem(ageItem);
		}
		verify(ageDistribution);
		return ageDistribution;
	}

	private void verify(AgeDistributionIfc ageDistribution) {
		int lastUpper = 0;
		for (AgeDistributionItem item : ageDistribution.getItems()) {
			if (lastUpper + 1 < item.lowerBound()) {
				throw new IllegalArgumentException(String
						.format("Distribution is not continuous. Missing item between %s and %s", lastUpper,
								item.lowerBound()));
			}
			lastUpper = item.upperBound();
		}
	}

	private AgeDistributionItem distributionItemFrom(String ageAsString) {
		String tmp = ageAsString.replaceFirst("age:[fm]:", "");
		String[] parts = tmp.split("-");
		int lowerBound = Integer.parseInt(parts[0]);
		int upperBound = (parts.length == 2) ? Integer.parseInt(parts[1]) : Integer.MAX_VALUE;
		int number = structuralData.valueOrDefault(ageAsString);
		return new AgeDistributionItem(lowerBound, upperBound, number);
	}

}
