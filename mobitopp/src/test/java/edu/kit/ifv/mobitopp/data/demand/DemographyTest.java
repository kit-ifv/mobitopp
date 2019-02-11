package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import nl.jqno.equalsverifier.EqualsVerifier;

public class DemographyTest {

	private EmploymentDistribution employment;
	private RangeDistributionIfc household;
	private RangeDistributionIfc femaleAge;
	private RangeDistributionIfc maleAge;
  private Map<AttributeType, RangeDistributionIfc> rangeDistributions;
  private Demography demandModel;

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(Demography.class).usingGetClass().verify();
	}

	@Before
	public void initialise() {
	  DefaultDistributions defaultDistributions = new DefaultDistributions();
		employment = EmploymentDistribution.createDefault();
		household = defaultDistributions.createHousehold();
		femaleAge = defaultDistributions.createFemaleAge();
		maleAge = defaultDistributions.createMaleAge();
		rangeDistributions = new LinkedHashMap<>();
		rangeDistributions.put(StandardAttribute.householdSize, household);
    rangeDistributions.put(StandardAttribute.maleAge, maleAge);
    rangeDistributions.put(StandardAttribute.femaleAge, femaleAge);
    demandModel = new Demography(employment, rangeDistributions);
	}

	@Test
	public void employment() {
		assertThat(demandModel.employment(), is(sameInstance(employment)));
	}

	@Test
	public void household() {
		assertThat(demandModel.household(), is(sameInstance(household)));
	}

	@Test
	public void femaleAge() {
		assertThat(demandModel.femaleAge(), is(sameInstance(femaleAge)));
	}

	@Test
	public void maleAge() {
		assertThat(demandModel.maleAge(), is(sameInstance(maleAge)));
	}
	
	@Test
	public void incrementHousehold() {
		int type = 1;
		int beforeIncrement = demandModel.household().amount(type);
		
		demandModel.incrementHousehold(type);
		int actualAmount = demandModel.household().amount(type);
		
		assertThat(actualAmount, is(equalTo(beforeIncrement + 1)));
	}
	
	@Test
	public void incrementEmployment() {
		Employment employment = Employment.FULLTIME;
		int beforeIncrement = demandModel.employment().amount(employment);
		
		demandModel.incrementEmployment(employment);
		int actualAmount = demandModel.employment().amount(employment);

		assertThat(actualAmount, is(equalTo(beforeIncrement + 1)));
	}
	
	@Test
  public void increment() {
    int size = 1;
    int beforeIncrement = demandModel.getDistribution(StandardAttribute.householdSize).amount(size);
    
    demandModel.increment(StandardAttribute.householdSize, size);
    int actualAmount = demandModel.getDistribution(StandardAttribute.householdSize).amount(size);
    
    assertThat(actualAmount, is(equalTo(beforeIncrement + 1)));
  }
	
	@Test
	public void incrementFemaleAge() {
		int age = 1;
		int beforeIncrement = demandModel.femaleAge().amount(age);
		
		demandModel.incrementAge(Gender.FEMALE, age);
		int actualAmount = demandModel.femaleAge().amount(age);
		
		assertThat(actualAmount, is(equalTo(beforeIncrement + 1)));
	}
	
	@Test
	public void incrementMaleAge() {
		int age = 1;
		int beforeIncrement = demandModel.maleAge().amount(age);
		
		demandModel.incrementAge(Gender.MALE, age);
		int actualAmount = demandModel.maleAge().amount(age);
		
		assertThat(actualAmount, is(equalTo(beforeIncrement + 1)));
	}
	
	@Test
  public void createsEmpty() {
    Demography empty = demandModel.createEmpty();
    
    assertThat(empty.femaleAge(), is(equalTo(femaleAge.createEmpty())));
  }
}
