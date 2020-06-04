package edu.kit.ifv.mobitopp.data.demand;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

	@BeforeEach
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
		assertThat(demandModel.employment()).isSameAs(employment);
	}

	@Test
	public void household() {
		assertThat(demandModel.household()).isSameAs(household);
	}

	@Test
	public void femaleAge() {
		assertThat(demandModel.femaleAge()).isSameAs(femaleAge);
	}

	@Test
	public void maleAge() {
		assertThat(demandModel.maleAge()).isSameAs(maleAge);
	}

	@Test
	public void incrementHousehold() {
		int type = 1;
		int beforeIncrement = demandModel.household().amount(type);

		demandModel.incrementHousehold(type);
		int actualAmount = demandModel.household().amount(type);

		assertThat(actualAmount).isEqualTo(beforeIncrement + 1);
	}

	@Test
	public void incrementEmployment() {
		Employment employment = Employment.FULLTIME;
		int beforeIncrement = demandModel.employment().amount(employment);

		demandModel.incrementEmployment(employment);
		int actualAmount = demandModel.employment().amount(employment);

		assertThat(actualAmount).isEqualTo(beforeIncrement + 1);
	}

	@Test
	public void increment() {
		int size = 1;
		int beforeIncrement = demandModel.getDistribution(StandardAttribute.householdSize).amount(size);

		demandModel.increment(StandardAttribute.householdSize, size);
		int actualAmount = demandModel.getDistribution(StandardAttribute.householdSize).amount(size);

		assertThat(actualAmount).isEqualTo(beforeIncrement + 1);
	}

	@Test
	void incrementUnknown() throws Exception {
		int income = 1;

		demandModel.increment(StandardAttribute.income, income);
		int actualAmount = demandModel.getDistribution(StandardAttribute.income).amount(income);

		assertThat(actualAmount).isEqualTo(1);
	}

	@Test
	public void incrementFemaleAge() {
		int age = 1;
		int beforeIncrement = demandModel.femaleAge().amount(age);

		demandModel.incrementAge(Gender.FEMALE, age);
		int actualAmount = demandModel.femaleAge().amount(age);

		assertThat(actualAmount).isEqualTo(beforeIncrement + 1);
	}

	@Test
	public void incrementMaleAge() {
		int age = 1;
		int beforeIncrement = demandModel.maleAge().amount(age);

		demandModel.incrementAge(Gender.MALE, age);
		int actualAmount = demandModel.maleAge().amount(age);

		assertThat(actualAmount).isEqualTo(beforeIncrement + 1);
	}

	@Test
	public void createsEmpty() {
		Demography empty = demandModel.createEmpty();

		assertThat(empty.femaleAge()).isEqualTo(femaleAge.createEmpty());
	}

	@Test
	void returnsEmptyDistribution() throws Exception {
		Demography emptyDemography = new Demography(employment, emptyMap());

		assertAll(EnumSet
				.complementOf(EnumSet.of(StandardAttribute.employment))
				.stream()
				.map(attribute -> () -> assertThat(emptyDemography.getDistribution(attribute))
						.as(attribute.attributeName())
						.isEqualTo(new RangeDistribution())));
	}

}
