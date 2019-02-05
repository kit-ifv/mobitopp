package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public class ProbabilityBasedSelector implements WeightedHouseholdSelector {

  private final Supplier<Double> random;

  public ProbabilityBasedSelector(Supplier<Double> random) {
    this.random = random;
  }

  @Override
  public List<WeightedHousehold> selectFrom(List<WeightedHousehold> households, int amount) {
    if (0 == amount) {
      return emptyList();
    }
    if (households.isEmpty()) {
      throw new IllegalArgumentException("No households available to select from.");
    }
    Map<WeightedHousehold, Double> weightedHouseholds = households
        .stream()
        .collect(toMap(Function.identity(), WeightedHousehold::weight));
    DiscreteRandomVariable<WeightedHousehold> distribution = new DiscreteRandomVariable<>(
        weightedHouseholds);
    List<WeightedHousehold> selectedHouseholds = new ArrayList<>();
    while(selectedHouseholds.size() < amount) {
      Double randomValue = random.get();
      WeightedHousehold selectedHousehold = distribution.realization(randomValue);
      selectedHouseholds.add(selectedHousehold);
    }
    return selectedHouseholds;
  }

}
