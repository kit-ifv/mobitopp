package edu.kit.ifv.mobitopp.data;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.IncomeDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;

public class DemandZoneTest {

	private static final int expectedAmount = 0;
	private static final int householdType = 1;
	private Zone zone;
	private Demography nominal;
	private EmploymentDistribution nominalEmployment;
	private HouseholdDistribution nominalHousehold;
	private FemaleAgeDistribution nominalFemale;
	private MaleAgeDistribution nominalMale;
  private IncomeDistribution nominalIncome;

	@Before
	public void initialise() {
		zone = mock(Zone.class);
		nominalEmployment = EmploymentDistribution.createDefault();
		nominalHousehold = new HouseholdDistribution();
		nominalFemale = new FemaleAgeDistribution();
		nominalMale = new MaleAgeDistribution();
		nominalIncome = new IncomeDistribution();
		nominal = new Demography(nominalEmployment, nominalHousehold, nominalFemale, nominalMale, nominalIncome);
	}

	@Test
	public void employmentDistribution() {
		EmploymentDistribution expectedEmployment = EmploymentDistribution.createDefault();
		DemandZone data = newDataFor(zone);

		EmploymentDistribution distribution = data.actualDemography().employment();

		assertThat(distribution, is(equalTo(expectedEmployment)));
	}

	@Test
	public void householdDistribution() {
		int nominalAmount = 2;
		nominalHousehold.addItem(householdItem(nominalAmount));
		DemandZone data = newDataFor(zone);

		HouseholdDistribution distribution = data.actualDemography().household();

		assertThat(distribution, is(equalTo(expectedHousehold())));
	}

	private HouseholdDistribution expectedHousehold() {
		HouseholdDistribution expectedHousehold = new HouseholdDistribution();
		HouseholdDistributionItem householdItem = householdItem(expectedAmount);
		expectedHousehold.addItem(householdItem);
		return expectedHousehold;
	}

	private HouseholdDistributionItem householdItem(int amount) {
		return new HouseholdDistributionItem(householdType, amount);
	}

	@Test
	public void femaleAgeDistribution() {
		int nominalAmount = 3;
		nominalFemale.addItem(newAgeItem(nominalAmount));
		DemandZone data = newDataFor(zone);

		ContinuousDistributionIfc distribution = data.actualDemography().femaleAge();

		assertThat(distribution, is(equalTo(expectedFemale())));
	}

	private FemaleAgeDistribution expectedFemale() {
		FemaleAgeDistribution expectedFemale = new FemaleAgeDistribution();
		expectedFemale.addItem(newAgeItem(expectedAmount));
		return expectedFemale;
	}

	private ContinuousDistributionItem newAgeItem(int amount) {
		return new ContinuousDistributionItem(0, 1, amount);
	}

	@Test
	public void maleAgeDistribution() {
		int nominalAmount = 4;
		nominalMale.addItem(newAgeItem(nominalAmount));
		DemandZone data = newDataFor(zone);

		ContinuousDistributionIfc distribution = data.actualDemography().maleAge();

		assertThat(distribution, is(equalTo(expectedMale())));
	}

	private MaleAgeDistribution expectedMale() {
		MaleAgeDistribution expectedMale = new MaleAgeDistribution();
		expectedMale.addItem(newAgeItem(expectedAmount));
		return expectedMale;
	}

	private DemandZone newDataFor(Zone zone) {
		return new DemandZone(zone, nominal);
	}
}
