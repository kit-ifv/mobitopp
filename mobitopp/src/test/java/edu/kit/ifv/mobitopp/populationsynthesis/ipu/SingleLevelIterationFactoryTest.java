package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;

@ExtendWith(MockitoExtension.class)
public class SingleLevelIterationFactoryTest {

	@Mock
	private SynthesisContext context;
  private DemandZone zone;
  private RangeDistributionIfc household;
  private RangeDistributionIfc femaleAge;
  private RangeDistributionIfc maleAge;
  private PanelDataRepository panelDataRepository;
  private RangeDistributionIfc income;

  @BeforeEach
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
		List<AttributeType> types = List
				.of(StandardAttribute.householdSize, StandardAttribute.income, StandardAttribute.femaleAge,
						StandardAttribute.maleAge);
		when(context.attributes(RegionalLevel.zone)).thenReturn(types);
		Iteration iteration = new SingleLevelIterationFactory(panelDataRepository, context)
				.createIterationFor(zone);

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
		when(context.attributes(RegionalLevel.zone)).thenReturn(types);
		Iteration iteration = new SingleLevelIterationFactory(panelDataRepository, context)
				.createIterationFor(zone);

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
		int requestedWeight = femaleAge.getItem(lower).amount();
    return new SimpleConstraint(name, requestedWeight);
  }

  private Constraint maleConstraintFor(int lower, int upper) {
    String name = name(StandardAttribute.maleAge, lower, upper);
		int requestedWeight = maleAge.getItem(lower).amount();
    return new SimpleConstraint(name, requestedWeight);
  }

  private Constraint householdConstraintFor(int type) {
    String name = name(StandardAttribute.householdSize, type, type);
		int requestedWeight = household.getItem(type).amount();
    return new SimpleConstraint(name, requestedWeight);
  }

  private Constraint incomeConstraintFor(int lower, int upper) {
    String name = name(StandardAttribute.income, lower, upper);
		int requestedWeight = income.getItem(lower).amount();
    return new SimpleConstraint(name, requestedWeight);
  }

	private String name(StandardAttribute attribute, int lower, int upper) {
		return "zone" + zone.getId().getExternalId() + NamedAttribute.nameSeparator + attribute.prefix()
				+ lower + "-" + upper;
	}
}
