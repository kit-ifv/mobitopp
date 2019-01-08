package edu.kit.ifv.mobitopp.dataimport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionItem;

public class ContinuousDistributionBuilder {

	private final StructuralData structuralData;
  private final String prefix;

	public ContinuousDistributionBuilder(StructuralData structuralData, String prefix) {
		super();
		this.structuralData = structuralData;
    this.prefix = prefix;
	}

	public <T extends ContinuousDistributionIfc> T buildFor(
			Supplier<T> continuousDistributionFactory) {
		T distribution = continuousDistributionFactory.get();
		List<String> values = new ArrayList<>();
		for (String attribute : structuralData.getAttributes()) {
			if (attribute.startsWith(prefix)) {
				values.add(attribute);
			}
		}
		for (int index = 0; index < values.size(); index++) {
			String value = values.get(index);
			ContinuousDistributionItem valueItem = distributionItemFrom(value);
			distribution.addItem(valueItem);
		}
		verify(distribution);
		return distribution;
	}

	private void verify(ContinuousDistributionIfc distribution) {
		int lastUpper = 0;
		for (ContinuousDistributionItem item : distribution.getItems()) {
			if (lastUpper + 1 < item.lowerBound()) {
				throw new IllegalArgumentException(String
						.format("Distribution is not continuous. Missing item between %s and %s", lastUpper,
								item.lowerBound()));
			}
			lastUpper = item.upperBound();
		}
	}

	private ContinuousDistributionItem distributionItemFrom(String header) {
		String tmp = header.replaceFirst(prefix, "");
		String[] parts = tmp.split("-");
		int lowerBound = Integer.parseInt(parts[0]);
		int upperBound = (parts.length == 2) ? Integer.parseInt(parts[1]) : Integer.MAX_VALUE;
		int number = structuralData.valueOrDefault(header);
		return new ContinuousDistributionItem(lowerBound, upperBound, number);
	}

}
