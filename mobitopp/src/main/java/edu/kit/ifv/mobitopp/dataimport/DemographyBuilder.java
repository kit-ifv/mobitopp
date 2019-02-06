package edu.kit.ifv.mobitopp.dataimport;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemographyBuilder {

  private static final AttributeType householdSize = StandardAttribute.householdSize;
  private static final AttributeType employment = StandardAttribute.employment;

  private final DemographyData demographyData;

  public DemographyBuilder(DemographyData demographyData) {
    super();
    this.demographyData = demographyData;
  }

  public Demography build(String forZoneId) {
    if (demographyData.hasData(forZoneId)) {
      return createDemography(forZoneId);
    }
    return createEmptyDemography();
  }

  Demography createEmptyDemography() {
    EmploymentDistribution employment = EmploymentDistribution.createDefault();
    HouseholdDistribution household = HouseholdDistribution.createDefault();
    Map<AttributeType, RangeDistributionIfc> rangeDistributions = continuousAttributes()
        .stream()
        .collect(Collectors
            .toMap(Function.identity(), item -> new RangeDistribution(), uniqueDistributions(),
                TreeMap::new));
    return new Demography(employment, household, rangeDistributions);
  }

  private List<AttributeType> continuousAttributes() {
    List<AttributeType> attributes = new ArrayList<>(demographyData.attributes());
    attributes.remove(employment);
    attributes.remove(householdSize);
    return attributes;
  }

  /**
   * @see Collectors#throwingMerger
   */
  private static <T> BinaryOperator<T> uniqueDistributions() {
    return (type, v) -> {
      throw new IllegalArgumentException(String.format("Duplicate attribute types: %s", type));
    };
  }

  private Demography createDemography(String zoneId) {
    HouseholdDistribution household = parseHouseholdDistribution(zoneId);
    EmploymentDistribution employment = parseJobDistribution(zoneId);
    Map<AttributeType, RangeDistributionIfc> rangeDistributions = parseDistributions(
        zoneId);
    return new Demography(employment, household, rangeDistributions);
  }

  private Map<AttributeType, RangeDistributionIfc> parseDistributions(String zoneId) {
    return continuousAttributes()
        .stream()
        .collect(toMap(Function.identity(), item -> parseDistribution(zoneId, item),
            uniqueDistributions(), TreeMap::new));
  }

  private HouseholdDistribution parseHouseholdDistribution(String zoneId) {
    StructuralData structuralData = demographyData.get(householdSize);
    return new HouseholdDistributionBuilder(structuralData, householdSize).build(zoneId);
  }

  private EmploymentDistribution parseJobDistribution(String zoneId) {
    if (demographyData.hasAttribute(employment)) {
      StructuralData structuralData = demographyData.get(employment);
      return new EmploymentDistributionBuilder(structuralData, employment).build(zoneId);
    }
    return EmploymentDistribution.createDefault();
  }

  private RangeDistributionIfc parseDistribution(String zoneId, AttributeType type) {
    StructuralData structuralData = demographyData.get(type);
    return new RangeDistributionBuilder(structuralData, type)
        .buildFor(zoneId, RangeDistribution::new);
  }
}
