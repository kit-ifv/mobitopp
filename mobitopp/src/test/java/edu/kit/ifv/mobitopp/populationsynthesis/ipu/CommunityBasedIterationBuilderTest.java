package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.populationsynthesis.community.SingleZone;

@ExtendWith(MockitoExtension.class)
public class CommunityBasedIterationBuilderTest {

	@Mock
	private PanelDataRepository panelData;

	private Community community;
	private RangeDistributionIfc household;
	private RangeDistributionIfc femaleAge;
	private RangeDistributionIfc maleAge;
	private RangeDistributionIfc income;

	@BeforeEach
	public void initialise() {
		ExampleDemandZones zones = ExampleDemandZones.create();
		DemandZone someZone = zones.someZone();
		community = new SingleZone(someZone);
		household = community.nominalDemography().getDistribution(StandardAttribute.householdSize);
		femaleAge = community.nominalDemography().getDistribution(StandardAttribute.femaleAge);
		maleAge = community.nominalDemography().getDistribution(StandardAttribute.maleAge);
		income = community.nominalDemography().getDistribution(StandardAttribute.income);
	}

	@Test
	public void createsIterationForVariousTypes() {
		List<AttributeType> types = asList(StandardAttribute.householdSize, StandardAttribute.income,
				StandardAttribute.femaleAge, StandardAttribute.maleAge);
		Iteration iteration = new CommunityBasedIterationBuilder(panelData, types).buildFor(community);

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
		Iteration iteration = new CommunityBasedIterationBuilder(panelData, types).buildFor(community);

		Iteration expectedIteration = createExpectedSingleTypeIteration();
		assertThat(iteration, is(equalTo(expectedIteration)));
	}

	private Iteration createExpectedSingleTypeIteration() {
		List<Constraint> constraints = new ArrayList<>();
		constraints.add(householdConstraintFor(1));
		constraints.add(householdConstraintFor(2));
		return new IpuIteration(constraints);
	}

	private Constraint femaleConstraintFor(final int lower, final int upper) {
		int amount = femaleAge.getItem(lower).amount();
		String name = nameOf(StandardAttribute.femaleAge, lower, upper);
		return new PersonConstraint(amount, name);
	}

	private Constraint maleConstraintFor(final int lower, final int upper) {
		int amount = maleAge.getItem(lower).amount();
		String name = nameOf(StandardAttribute.maleAge, lower, upper);
		return new PersonConstraint(amount, name);
	}

	private Constraint householdConstraintFor(final int type) {
		int amount = household.getItem(type).amount();
		String name = nameOf(StandardAttribute.householdSize, type, type);
		return new HouseholdConstraint(amount, name);
	}

	private Constraint incomeConstraintFor(final int lower, final int upper) {
		int amount = income.getItem(lower).amount();
		String name = nameOf(StandardAttribute.income, lower, upper);
		return new HouseholdConstraint(amount, name);
	}

	private String nameOf(final StandardAttribute attribute, final int lower, final int upper) {
		return "community-" + community.getId() + NamedAttribute.nameSeparator + attribute.prefix()
				+ lower + "-" + upper;
	}
}
