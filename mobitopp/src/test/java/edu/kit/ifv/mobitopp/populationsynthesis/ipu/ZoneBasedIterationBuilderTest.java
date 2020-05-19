package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;

public class ZoneBasedIterationBuilderTest {

  private DemandZone zone;
  private RangeDistributionIfc household;
  private RangeDistributionIfc femaleAge;
  private RangeDistributionIfc maleAge;
  private PanelDataRepository panelDataRepository;
  private RangeDistributionIfc income;

  @Before
  public void initialise() {
    ExampleDemandZones zones = ExampleDemandZones.create();
    zone = zones.someZone();
    household = zone.nominalDemography().household();
    femaleAge = zone.nominalDemography().femaleAge();
    maleAge = zone.nominalDemography().maleAge();
    income = zone.nominalDemography().income();
    panelDataRepository = mock(PanelDataRepository.class);
  }

  @Test
  public void createsIterationForVariousTypes() {
    List<AttributeType> types = asList(StandardAttribute.householdSize, StandardAttribute.income,
        StandardAttribute.femaleAge, StandardAttribute.maleAge);
    Iteration iteration = new ZoneBasedIterationBuilder(panelDataRepository, types).buildFor(zone);

    Iteration expectedIteration = createExpectedIteration();
    assertThat(iteration, is(equalTo(expectedIteration)));
  }

  private Iteration createExpectedIteration() {
    List<Constraint> constraints = new ArrayList<>();
    constraints.add(householdConstraintFor(1));
    constraints.add(householdConstraintFor(2));
    constraints.add(incomeConstraintFor(0, 1000));
    constraints.add(incomeConstraintFor(1001, 2000));
    constraints.add(incomeConstraintFor(2001, Integer.MAX_VALUE));
    constraints.add(femaleConstraintFor(0, 5));
    constraints.add(femaleConstraintFor(6, Integer.MAX_VALUE));
    constraints.add(maleConstraintFor(0, 10));
    constraints.add(maleConstraintFor(11, Integer.MAX_VALUE));
    return new IpuIteration(constraints);
  }

  @Test
  public void createsIterationForSingleType() {
    List<AttributeType> types = asList(StandardAttribute.householdSize);
    Iteration iteration = new ZoneBasedIterationBuilder(panelDataRepository, types).buildFor(zone);

    Iteration expectedIteration = createExpectedSingleTypeIteration();
    assertThat(iteration, is(equalTo(expectedIteration)));
  }

  private Iteration createExpectedSingleTypeIteration() {
    List<Constraint> constraints = new ArrayList<>();
    constraints.add(householdConstraintFor(1));
    constraints.add(householdConstraintFor(2));
    return new IpuIteration(constraints);
  }

  private Constraint femaleConstraintFor(int lower, int upper) {
    String name = name(StandardAttribute.femaleAge, lower, upper);
		return new PersonConstraint(femaleAge.getItem(lower).amount(), name);
  }

  private Constraint maleConstraintFor(int lower, int upper) {
    String name = name(StandardAttribute.maleAge, lower, upper);
		return new PersonConstraint(maleAge.getItem(lower).amount(), name);
  }

  private Constraint householdConstraintFor(int type) {
    String name = name(StandardAttribute.householdSize, type, type);
		return new HouseholdConstraint(household.getItem(type).amount(), name);
  }

  private Constraint incomeConstraintFor(int lower, int upper) {
    String name = name(StandardAttribute.income, lower, upper);
		return new HouseholdConstraint(income.getItem(lower).amount(), name);
  }

	private String name(StandardAttribute attribute, int lower, int upper) {
		return "zone" + zone.getId().getExternalId() + NamedAttribute.nameSeparator + attribute.prefix()
				+ lower + "-" + upper;
	}
}
