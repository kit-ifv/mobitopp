package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DynamicHouseholdAttribute;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.FemaleAge;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.HouseholdSize;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.MaleAge;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public enum StandardAttribute implements AttributeType {

  householdSize("household_size") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return demography.household().items().map(item -> new HouseholdSize(prefix(), item.type()));
    }
  },
  income("income") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return demography
          .income()
          .items()
          .map(item -> new DynamicHouseholdAttribute(prefix(), item.lowerBound(), item.upperBound(),
              HouseholdOfPanelData::income, Demography::income));
    }
  },
  femaleAge("age_f") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return demography
          .femaleAge()
          .items()
          .map(item -> new FemaleAge(prefix(), item.lowerBound(), item.upperBound()));
    }
  },
  maleAge("age_m") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return demography
          .maleAge()
          .items()
          .map(item -> new MaleAge(prefix(), item.lowerBound(), item.upperBound()));
    }
  },
  employment("job") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return Stream.empty();
    }
  }, distance("distance") {

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
  public abstract Stream<Attribute> createAttributes(Demography demography);
}