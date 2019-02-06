package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public enum StandardAttribute implements AttributeType {

  householdType("HHTyp") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return createAttributes(demography, HouseholdOfPanelData::domCode);
    }
  },
  householdSize("household_size") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return createAttributes(demography, HouseholdOfPanelData::size);
    }
  },
  income("income") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return createAttributes(demography, HouseholdOfPanelData::income);
    }
  },
  femaleAge("age_f") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return demography
          .femaleAge()
          .items()
          .map(item -> new FemaleAge(this, item.lowerBound(), item.upperBound()));
    }
  },
  maleAge("age_m") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return demography
          .maleAge()
          .items()
          .map(item -> new MaleAge(this, item.lowerBound(), item.upperBound()));
    }
  },
  employment("job") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return Stream.empty();
    }
  },
  distance("distance") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return Stream.empty();
    }
  };

  private final String attributeName;

  private StandardAttribute(String attributeName) {
    this.attributeName = attributeName;
  }

  @Override
  public String attributeName() {
    return attributeName;
  }

  @Override
  public String prefix() {
    return attributeName + ":";
  }
  
  @Override
  public String createInstanceName(int lowerBound, int upperBound) {
    return prefix() + lowerBound + "-" + upperBound;
  }
  
  @Override
  public String createInstanceName(RangeDistributionItem item) {
    return createInstanceName(item.lowerBound(), item.upperBound());
  }

  Stream<Attribute> createAttributes(
      Demography demography, Function<HouseholdOfPanelData, Integer> valueOfHousehold) {
    return demography
        .getDistribution(this)
        .items()
        .map(item -> createHouseholdAttribute(valueOfHousehold, item));
  }

  private DynamicHouseholdAttribute createHouseholdAttribute(
      Function<HouseholdOfPanelData, Integer> valueOfHousehold, RangeDistributionItem item) {
    return new DynamicHouseholdAttribute(this, item.lowerBound(), item.upperBound(),
        valueOfHousehold, valueOfDemography());
  }

  private Function<Demography, RangeDistributionIfc> valueOfDemography() {
    return demo -> demo.getDistribution(this);
  }

  @Override
  public abstract Stream<Attribute> createAttributes(Demography demography);
}