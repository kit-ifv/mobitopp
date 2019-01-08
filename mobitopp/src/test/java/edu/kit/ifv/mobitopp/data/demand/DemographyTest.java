package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import nl.jqno.equalsverifier.EqualsVerifier;

public class DemographyTest {

	private EmploymentDistribution employment;
	private HouseholdDistribution household;
	private FemaleAgeDistribution femaleAge;
	private MaleAgeDistribution maleAge;
  private IncomeDistribution income;

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(Demography.class).usingGetClass().verify();
	}

	@Before
	public void initialise() {
	  DefaultDistributions defaultDistributions = new DefaultDistributions();
		employment = EmploymentDistribution.createDefault();
		household = HouseholdDistribution.createDefault();
		femaleAge = defaultDistributions.createFemaleAge();
		maleAge = defaultDistributions.createMaleAge();
		income = defaultDistributions.createIncome();
	}

	@Test
	public void employment() {
		assertThat(demandModel().employment(), is(sameInstance(employment)));
	}

	@Test
	public void household() {
		assertThat(demandModel().household(), is(sameInstance(household)));
	}

	@Test
	public void femaleAge() {
		assertThat(demandModel().femaleAge(), is(sameInstance(femaleAge)));
	}

	@Test
	public void maleAge() {
		assertThat(demandModel().maleAge(), is(sameInstance(maleAge)));
	}
	
	@Test
	public void income() {
	  assertThat(demandModel().income(), is(sameInstance(income)));
	}
	
	@Test
	public void incrementHousehold() {
		int type = 1;
		Demography demandModel = demandModel();
		int beforeIncrement = demandModel.household().getItem(type).amount();
		
		demandModel.incrementHousehold(type);
		int actualAmount = demandModel.household().getItem(type).amount();
		
		assertThat(actualAmount, is(equalTo(beforeIncrement + 1)));
	}
	
	@Test
	public void incrementEmployment() {
		Employment employment = Employment.FULLTIME;
		Demography demandModel = demandModel();
		int beforeIncrement = demandModel.employment().getItem(employment).amount();
		
		demandModel.incrementEmployment(employment);
		int actualAmount = demandModel.employment().getItem(employment).amount();

		assertThat(actualAmount, is(equalTo(beforeIncrement + 1)));
	}
	
	@Test
	public void incrementFemaleAge() {
		int age = 1;
		Demography demandModel = demandModel();
		int beforeIncrement = demandModel.femaleAge().getItem(age).amount();
		
		demandModel.incrementAge(Gender.FEMALE, age);
		int actualAmount = demandModel.femaleAge().getItem(age).amount();
		
		assertThat(actualAmount, is(equalTo(beforeIncrement + 1)));
	}
	
	@Test
	public void incrementMaleAge() {
		int age = 1;
		Demography demandModel = demandModel();
		int beforeIncrement = demandModel.maleAge().getItem(age).amount();
		
		demandModel.incrementAge(Gender.MALE, age);
		int actualAmount = demandModel.maleAge().getItem(age).amount();
		
		assertThat(actualAmount, is(equalTo(beforeIncrement + 1)));
	}

	private Demography demandModel() {
		return new Demography(employment, household, femaleAge, maleAge, income);
	}
}
