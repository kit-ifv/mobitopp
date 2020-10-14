package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WeightedHouseholdReproducer implements HouseholdReproducer {

	private final List<Attribute> attributes;
	private final WeightedHouseholdSelector householdSelector;
  private final PanelDataRepository panelData;

	@Override
	public Stream<WeightedHousehold> getHouseholdsToCreate(List<WeightedHousehold> households) {
		return attributes.stream().flatMap(attribute -> householdsFor(attribute, households));
	}

	private Stream<WeightedHousehold> householdsFor(
			Attribute attribute, List<WeightedHousehold> households) {
		List<WeightedHousehold> possibleHouseholds = filterBy(attribute, households);
		int totalSum = calculateTotalSum(possibleHouseholds);
		return householdSelector.selectFrom(possibleHouseholds, totalSum).stream();
	}

	private int calculateTotalSum(List<WeightedHousehold> possibleHouseholds) {
		int totalSum = Math
				.toIntExact(
						Math.round(possibleHouseholds.stream().mapToDouble(WeightedHousehold::weight).sum()));
		return totalSum;
	}

	private List<WeightedHousehold> filterBy(
			Attribute attribute, List<WeightedHousehold> households) {
		List<WeightedHousehold> possibleHouseholds = households
				.stream()
				.filter(household -> 0 < attribute.valueFor(household.household(), panelData))
				.collect(toList());
		return possibleHouseholds;
	}

}
