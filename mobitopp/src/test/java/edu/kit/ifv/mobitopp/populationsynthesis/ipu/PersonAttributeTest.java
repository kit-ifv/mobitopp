package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder.householdOfPanelData;
import static edu.kit.ifv.mobitopp.populationsynthesis.PersonOfPanelDataBuilder.personOfPanelData;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public class PersonAttributeTest {

  private AttributeType attributeType;
  private int lowerBound;
  private int upperBound;
  private Function<PersonOfPanelData, Integer> personValue;
  private int amount;

  @Before
  public void initialise() {
    attributeType = StandardAttribute.distance;
    lowerBound = 0;
    upperBound = 1;
    amount = 2;
    personValue = person -> (int) person.getPoleDistance();
  }

  @Test
  public void valueForNotMatchingPeople() {
    HouseholdOfPanelData household = householdOfPanelData().build();
    PanelDataRepository panelDataRepository = mock(PanelDataRepository.class);

    PersonAttribute attribute = newPersonAttribute();

    int value = attribute.valueFor(household, panelDataRepository);

    assertThat(value, is(equalTo(0)));
  }

  @Test
  public void valueForMatchingPeople() {
    float matchingDistance = 1f;
    PersonOfPanelData somePerson = personOfPanelData().withPoleDistance(matchingDistance).build();
    PersonOfPanelData otherPerson = personOfPanelData().withPoleDistance(matchingDistance).build();
    List<PersonOfPanelData> people = asList(somePerson, otherPerson);
    HouseholdOfPanelData household = householdOfPanelData().build();
    PanelDataRepository panelDataRepository = mock(PanelDataRepository.class);
    when(panelDataRepository.getPersonsOfHousehold(household.id())).thenReturn(people);

    PersonAttribute attribute = newPersonAttribute();

    int value = attribute.valueFor(household, panelDataRepository);

    assertThat(value, is(equalTo(2)));
  }

  @Test
  public void createConstraint() {
    Demography demography = createDemography();
    PersonAttribute attribute = newPersonAttribute();

    Constraint constraint = attribute.createConstraint(demography);

    Constraint expectedConstraint = new PersonConstraint(amount,
        attributeType.createInstanceName(lowerBound, upperBound));
    assertThat(constraint, is(equalTo(expectedConstraint)));
  }

  private Demography createDemography() {
    EmploymentDistribution employment = EmploymentDistribution.createDefault();
    RangeDistributionIfc distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(lowerBound, upperBound, amount));
    Map<AttributeType, RangeDistributionIfc> distributions = singletonMap(attributeType,
        distribution);
    return new Demography(employment, distributions);
  }

  private PersonAttribute newPersonAttribute() {
    return new PersonAttribute(attributeType, lowerBound, upperBound, personValue);
  }
}