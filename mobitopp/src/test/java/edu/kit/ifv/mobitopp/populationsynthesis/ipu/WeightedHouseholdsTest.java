package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class WeightedHouseholdsTest {

  private static final short year = 2020;
  private static final String attribute = "attribute";
  private static final double requestedWeight = 6.0d;
  private static final HouseholdOfPanelDataId someId = new HouseholdOfPanelDataId(year, 1);
  private static final HouseholdOfPanelDataId otherId = new HouseholdOfPanelDataId(year, 2);
  
  @Test
  void copyContent() throws Exception {
    WeightedHousehold someHousehold = newHousehold(someId);
    WeightedHousehold otherHousehold = newHousehold(otherId);
    WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, otherHousehold));

    WeightedHouseholds copy = households.deepCopy();

    assertThat(households).isEqualTo(copy);
    assertThat(households).isNotSameAs(copy);
    assertAll(households
        .toList()
        .stream()
        .map(
            household -> () -> assertThat(copy.toList()).noneMatch(copied -> copied == household)));
  }

  private WeightedHousehold newHousehold(HouseholdOfPanelDataId id) {
    return new WeightedHousehold(id, 1.0d, attributes(),
        new DefaultRegionalContext(RegionalLevel.community, "1"));
  }

  private Map<String, Integer> attributes() {
    return emptyMap();
  }

  @Test
  public void updateWeightsOnAllHousehold() {
    WeightedHousehold someHousehold = newHousehold(someId, 1.0d);
    WeightedHousehold anotherHousehold = newHousehold(otherId, 2.0d);
    WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, anotherHousehold));
    SimpleConstraint constraint = newConstraint();

    WeightedHouseholds updatedHouseholds = constraint.scaleWeightsOf(households);

    Map<HouseholdOfPanelDataId, Double> idToWeight = updatedHouseholds
        .toList()
        .stream()
        .collect(toMap(WeightedHousehold::id, WeightedHousehold::weight));
    assertThat(idToWeight).containsEntry(someId, 2.0d).containsEntry(otherId, 4.0d);
  }
  
  @Test
  void preservesScaledWeightAfterCopy() throws Exception {
    WeightedHousehold someHousehold = newHousehold(someId, 1.0d);
    WeightedHousehold anotherHousehold = newHousehold(otherId, 2.0d);
    WeightedHouseholds households = new WeightedHouseholds(asList(someHousehold, anotherHousehold));
    SimpleConstraint constraint = newConstraint();

    WeightedHouseholds updatedHouseholds = constraint.scaleWeightsOf(households);
    WeightedHouseholds scaledAndCopied = updatedHouseholds.deepCopy();

    Map<HouseholdOfPanelDataId, Double> idToWeight = scaledAndCopied
        .toList()
        .stream()
        .collect(toMap(WeightedHousehold::id, WeightedHousehold::weight));
    assertThat(idToWeight).containsEntry(someId, 2.0d).containsEntry(otherId, 4.0d);
  }

  @Test
  public void updateWeightOnSingleHousehold() {
    String otherAttribute = "other";
    WeightedHousehold someHousehold = newHousehold(someId, 1.0d, otherAttribute);
    WeightedHousehold anotherHousehold = newHousehold(otherId, 2.0d);
    WeightedHouseholds households = new WeightedHouseholds(asList(anotherHousehold, someHousehold));
    SimpleConstraint constraint = newConstraint();

    WeightedHouseholds updatedHouseholds = constraint.scaleWeightsOf(households);

    Map<HouseholdOfPanelDataId, Double> idToWeight = updatedHouseholds
        .toList()
        .stream()
        .collect(toMap(WeightedHousehold::id, WeightedHousehold::weight));
    assertThat(idToWeight).containsEntry(someId, 1.0d).containsEntry(otherId, 6.0d);
  }

  @Test
  public void arrayUpdateWeightsOnAllHousehold() {
    WeightedHousehold someHousehold = newHousehold(someId, 1.0d);
    WeightedHousehold anotherHousehold = newHousehold(otherId, 2.0d);
    ArrayWeightedHouseholds households = new ArrayWeightedHouseholds(
        asList(someHousehold, anotherHousehold));
    SimpleConstraint constraint = newConstraint();

    ArrayWeightedHouseholds updatedHouseholds = households.scale(attribute, requestedWeight);

    Map<HouseholdOfPanelDataId, Double> idToWeight = updatedHouseholds
        .toList()
        .stream()
        .collect(toMap(WeightedHousehold::id, WeightedHousehold::weight));
    assertThat(idToWeight).containsEntry(someId, 2.0d).containsEntry(otherId, 4.0d);
  }

  @Test
  public void arrayUpdateWeightOnSingleHousehold() {
    String otherAttribute = "other";
    WeightedHousehold someHousehold = newHousehold(someId, 1.0d, otherAttribute);
    WeightedHousehold anotherHousehold = newHousehold(otherId, 2.0d);
    ArrayWeightedHouseholds households = new ArrayWeightedHouseholds(
        asList(anotherHousehold, someHousehold));
    SimpleConstraint constraint = newConstraint();

    ArrayWeightedHouseholds updatedHouseholds = households.scale(attribute, requestedWeight);

    Map<HouseholdOfPanelDataId, Double> idToWeight = updatedHouseholds
        .toList()
        .stream()
        .collect(toMap(WeightedHousehold::id, WeightedHousehold::weight));
    assertThat(idToWeight).containsEntry(someId, 1.0d).containsEntry(otherId, 6.0d);
  }

  private SimpleConstraint newBaseConstraint(double weight) {
    return new SimpleConstraint(attribute, weight);
  }

  private WeightedHousehold newHousehold(HouseholdOfPanelDataId id, double weight) {
    return newHousehold(id, weight, attribute);
  }

  private WeightedHousehold newHousehold(
      HouseholdOfPanelDataId id, double weight, String attribute) {
    return new WeightedHousehold(id, weight, attributes(attribute),
        new DefaultRegionalContext(RegionalLevel.community, "1"));
  }

  private Map<String, Integer> attributes(String attribute) {
    return Map.of(attribute, 1);
  }

  private SimpleConstraint newConstraint() {
    return new SimpleConstraint(attribute, requestedWeight);
  }
}
