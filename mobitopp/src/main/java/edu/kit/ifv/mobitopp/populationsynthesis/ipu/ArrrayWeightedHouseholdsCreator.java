package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class ArrrayWeightedHouseholdsCreator {

  private static final int startOffset = 0;
  private final SynthesisContext context;
  private final PanelDataRepository panelData;

  public ArrrayWeightedHouseholdsCreator(SynthesisContext context, PanelDataRepository panelData) {
    this.context = context;
    this.panelData = panelData;
  }

  public WeightedHouseholds createFor(DemandRegion region) {
    List<HouseholdOfPanelData> households = panelData.getHouseholds();
    List<DemandZone> zones = region.zones().collect(toList());
    int numberOfZones = Math.toIntExact(zones.size());
    int numberOfHouseholds = households.size();
    double[] weights = new double[numberOfZones * numberOfHouseholds];
    for (int index = 0; index < weights.length; index++) {
      weights[index] = 1.0d;
    }
    List<Attribute> attributes = new ArrayList<>();
    Map<RegionalLevel, List<RequestedWeights>> requestedWeightsMapping = new LinkedHashMap<>();
    createRequestedWeights(region, startOffset, attributes, requestedWeightsMapping,
        numberOfHouseholds, startOffset);
    ensureAllLevelsArePresent(requestedWeightsMapping);
    int[][] householdValues = createHouseholdValues(households, attributes);
    return new WeightedHouseholds(households, weights, requestedWeightsMapping, householdValues,
        zones);
  }

  private void ensureAllLevelsArePresent(
      Map<RegionalLevel, List<RequestedWeights>> requestedWeightsMapping) {
    EnumSet<RegionalLevel> levels = EnumSet.allOf(RegionalLevel.class);
    levels.removeAll(requestedWeightsMapping.keySet());
    levels.forEach(level -> requestedWeightsMapping.put(level, List.of()));
  }

  private int[][] createHouseholdValues(
      List<HouseholdOfPanelData> households, List<Attribute> attributes) {
    int[][] householdValues = new int[households.size()][attributes.size()];
    for (int householdIndex = 0; householdIndex < households.size(); householdIndex++) {
      HouseholdOfPanelData household = households.get(householdIndex);
      for (int attributeIndex = 0; attributeIndex < attributes.size(); attributeIndex++) {
        Attribute attribute = attributes.get(attributeIndex);
        householdValues[householdIndex][attributeIndex] = attribute.valueFor(household, panelData);
      }
    }
    return householdValues;
  }

  private void createRequestedWeights(
      DemandRegion region, int attributeOffset, List<Attribute> attributes,
      Map<RegionalLevel, List<RequestedWeights>> requestedWeightsMapping, int numberOfHouseholds,
      int weightOffset) {
    List<AttributeType> districtAttributes = context.attributes(region.regionalLevel());
    List<Attribute> newAttributes = new ArrayList<>();
    for (AttributeType attribute : districtAttributes) {
      Demography nominalDemography = region.nominalDemography();
      attribute
          .createAttributes(nominalDemography, region.getRegionalContext())
          .forEach(newAttributes::add);
    }
    attributes.addAll(newAttributes);
    int[] requestedWeights = new int[newAttributes.size()];
    for (int index = 0; index < newAttributes.size(); index++) {
      Attribute attribute = newAttributes.get(index);
      requestedWeights[index] = attribute.requestedWeight();
    }
    int numberOfWeights = countZonesOf(region) * numberOfHouseholds;
    RequestedWeights newRequestedWeights = new RequestedWeights(requestedWeights, attributeOffset,
        weightOffset, numberOfWeights);
    requestedWeightsMapping
        .merge(region.regionalLevel(),
            List.of(newRequestedWeights),
            ArrrayWeightedHouseholdsCreator::mergeLists);

    int nextLevelAttributeOffset = attributes.size();
    int nextLevelWeightOffset = 0;
    for (DemandRegion part : region.parts()) {
      createRequestedWeights(part, nextLevelAttributeOffset, attributes, requestedWeightsMapping,
          numberOfHouseholds, nextLevelWeightOffset);
      nextLevelWeightOffset += countZonesOf(part) * numberOfHouseholds;
    }
  }

  private int countZonesOf(DemandRegion part) {
    return Math.toIntExact(part.zones().count());
  }

  private static <T> List<T> mergeLists(List<T> list1, List<T> list2) {
    ArrayList<T> newList = new ArrayList<>(list1);
    newList.addAll(list2);
    return newList;
  }
}
