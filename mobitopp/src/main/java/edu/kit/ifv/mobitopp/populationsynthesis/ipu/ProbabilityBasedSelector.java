package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.toLinkedMap;
import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProbabilityBasedSelector implements WeightedHouseholdSelector {

  private final DoubleSupplier random;

  public ProbabilityBasedSelector(DoubleSupplier random) {
    this.random = random;
  }

  @Override
  public List<WeightedHousehold> selectFrom(List<WeightedHousehold> households, int amount) {
    if (0 == amount) {
      return emptyList();
    }
    if (households.isEmpty()) {
      throw warn(new IllegalArgumentException("No households available to select from."), log);
    }
    return doSelect(households, amount);
  }

  private List<WeightedHousehold> doSelect(List<WeightedHousehold> households, int amount) {
    Map<WeightedHousehold, Double> weightedHouseholds = households
        .stream()
        .collect(toLinkedMap(Function.identity(), WeightedHousehold::weight));
    DiscreteRandomVariable<WeightedHousehold> distribution = new DiscreteRandomVariable<>(
        weightedHouseholds);
    List<WeightedHousehold> selectedHouseholds = new ArrayList<>();
    while(selectedHouseholds.size() < amount) {
      double randomValue = random.getAsDouble();
      WeightedHousehold selectedHousehold = distribution.realization(randomValue);
      selectedHouseholds.add(selectedHousehold);
    }
    return selectedHouseholds;
  }

}
