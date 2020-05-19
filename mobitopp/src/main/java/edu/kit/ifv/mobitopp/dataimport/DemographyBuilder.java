package edu.kit.ifv.mobitopp.dataimport;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.community.DemographyRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.community.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemographyBuilder implements DemographyRepository {

  private static final AttributeType employment = StandardAttribute.employment;

  private final DemographyData demographyData;

  public DemographyBuilder(DemographyData demographyData) {
    super();
    this.demographyData = demographyData;
  }
  
  @Override
  public Demography getDemographyFor(RegionalLevel level, String id) {
  	if (demographyData.hasData(level, id)) {
		  return createDemography(level, id);
		}
		return createEmptyDemography(level);
  }

  Demography createEmptyDemography(RegionalLevel level) {
    EmploymentDistribution employment = EmploymentDistribution.createDefault();
    Map<AttributeType, RangeDistributionIfc> rangeDistributions = rangeAttributes(level)
        .stream()
        .collect(Collectors
            .toMap(Function.identity(), item -> new RangeDistribution(), uniqueDistributions(),
                TreeMap::new));
    return new Demography(employment, rangeDistributions);
  }

  private List<AttributeType> rangeAttributes(RegionalLevel level) {
    List<AttributeType> attributes = new ArrayList<>(demographyData.attributes(level));
    attributes.remove(employment);
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

  private Demography createDemography(RegionalLevel level, String zoneId) {
    EmploymentDistribution employment = parseJobDistribution(level, zoneId);
    Map<AttributeType, RangeDistributionIfc> rangeDistributions = parseDistributions(
        level, zoneId);
    return new Demography(employment, rangeDistributions);
  }

	private Map<AttributeType, RangeDistributionIfc> parseDistributions(
			RegionalLevel level, String zoneId) {
    return rangeAttributes(level)
        .stream()
        .collect(toMap(Function.identity(), item -> parseDistribution(level, zoneId, item),
            uniqueDistributions(), TreeMap::new));
  }

  private EmploymentDistribution parseJobDistribution(RegionalLevel level, String zoneId) {
    if (demographyData.hasAttribute(level, employment)) {
      StructuralData structuralData = demographyData.get(level, employment);
      return new EmploymentDistributionBuilder(structuralData, employment).build(zoneId);
    }
    return EmploymentDistribution.createDefault();
  }

  private RangeDistributionIfc parseDistribution(RegionalLevel level, String zoneId, AttributeType type) {
    StructuralData structuralData = demographyData.get(level, type);
    return new RangeDistributionBuilder(structuralData, type)
        .buildFor(zoneId, RangeDistribution::new);
  }
}
