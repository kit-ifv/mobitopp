package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public enum StandardAttribute implements AttributeType {

  domCode("dom_code") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return createHouseholdAttributes(demography, HouseholdOfPanelData::domCode);
    }
  },
  householdSize("household_size") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return createHouseholdAttributes(demography, HouseholdOfPanelData::size);
    }
  },
	householdType("household_type") {
	
	  @Override
	  public Stream<Attribute> createAttributes(Demography demography) {
	    return createHouseholdAttributes(demography, HouseholdOfPanelData::type);
	  }
	},
  income("income") {

    @Override
    public Stream<Attribute> createAttributes(Demography demography) {
      return createHouseholdAttributes(demography, HouseholdOfPanelData::income);
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
      return createPersonAttributes(demography, person -> (int) person.getPoleDistance());
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

  Stream<Attribute> createPersonAttributes(
      Demography demography, Function<PersonOfPanelData, Integer> valueOfPerson) {
    return demography
        .getDistribution(this)
        .items()
        .map(item -> createPersonAttribute(valueOfPerson, item));
  }

  private PersonAttribute createPersonAttribute(
      Function<PersonOfPanelData, Integer> personValue, RangeDistributionItem item) {
    return new PersonAttribute(this, item.lowerBound(), item.upperBound(), personValue);
  }

  Stream<Attribute> createHouseholdAttributes(
      Demography demography, Function<HouseholdOfPanelData, Integer> valueOfHousehold) {
    return demography
        .getDistribution(this)
        .items()
        .map(item -> createHouseholdAttribute(valueOfHousehold, item));
  }

  private DynamicHouseholdAttribute createHouseholdAttribute(
      Function<HouseholdOfPanelData, Integer> valueOfHousehold, RangeDistributionItem item) {
    return new DynamicHouseholdAttribute(this, item.lowerBound(), item.upperBound(),
        valueOfHousehold);
  }

  @Override
  public abstract Stream<Attribute> createAttributes(Demography demography);
}