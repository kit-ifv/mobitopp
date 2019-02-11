package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class PersonAttribute implements Attribute {

  private final AttributeType attributeType;
  private final int lowerBound;
  private final int upperBound;
  private final Function<PersonOfPanelData, Integer> personValue;

  public PersonAttribute(
      AttributeType attributeType, int lowerBound, int upperBound,
      Function<PersonOfPanelData, Integer> personValue) {
    super();
    this.attributeType = attributeType;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.personValue = personValue;
  }

  @Override
  public Constraint createConstraint(Demography demography) {
    int requestedWeight = demography.getDistribution(attributeType).amount(lowerBound);
    return new PersonConstraint(requestedWeight, name());
  }

  @Override
  public int valueFor(HouseholdOfPanelData household, PanelDataRepository panelDataRepository) {
    List<PersonOfPanelData> persons = panelDataRepository.getPersonsOfHousehold(household.id());
    return Math
        .toIntExact(persons
            .stream()
            .filter(person -> personValue.apply(person) >= lowerBound)
            .filter(person -> personValue.apply(person) <= upperBound)
            .count());
  }

  @Override
  public String name() {
    return attributeType.createInstanceName(lowerBound, upperBound);
  }

}
