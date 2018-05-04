package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem.Type;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;
import edu.kit.ifv.mobitopp.simulation.Employment;

public class DemographyBuilderTest {

	private StructuralData demographyData;
	private DemographyBuilder builder;

	@Before
	public void initialise() {
		demographyData = Example.demographyData();
		builder = new DemographyBuilder(demographyData);
	}

	@Test
	public void buildHouseholdDistribution() {
		assertThat(demography().household(), is(equalTo(expectedHouseholds())));
	}

	@Test
	public void buildFemaleAgeDistribution() {
		assertThat(demography().femaleAge(), is(equalTo(expectedFemale())));
	}
	
	@Test
	public void buildMaleAgeDistribution() {
		assertThat(demography().maleAge(), is(equalTo(expectedMale())));
	}
	
	@Test
	public void buildEmploymentDistribution() {
		assertThat(demography().employment(), is(equalTo(expectedEmployment())));		
	}

	private Demography demography() {
		return builder.build();
	}

	private HouseholdDistribution expectedHouseholds() {
		HouseholdDistribution distribution = new HouseholdDistribution();
		distribution.addItem(new HouseholdDistributionItem(1, 161));
		distribution.addItem(new HouseholdDistributionItem(2, 508));
		distribution.addItem(new HouseholdDistributionItem(3, 29));
		distribution.addItem(new HouseholdDistributionItem(4, 16));
		distribution.addItem(new HouseholdDistributionItem(5, 402));
		distribution.addItem(new HouseholdDistributionItem(6, 287));
		distribution.addItem(new HouseholdDistributionItem(7, 4));
		distribution.addItem(new HouseholdDistributionItem(8, 108));
		distribution.addItem(new HouseholdDistributionItem(9, 169));
		distribution.addItem(new HouseholdDistributionItem(10, 6));
		distribution.addItem(new HouseholdDistributionItem(11, 122));
		distribution.addItem(new HouseholdDistributionItem(12, 233));
		return distribution;
	}

	private FemaleAgeDistribution expectedFemale() {
		FemaleAgeDistribution distribution = new FemaleAgeDistribution();
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 5, 113));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 9, 79));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 15, 155));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 18, 85));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 24, 155));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 29, 92));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 44, 445));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 59, 516));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 64, 137));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 74, 308));
		distribution.addItem(new AgeDistributionItem(Type.OVER, 75, 198));
		return distribution;
	}

	private MaleAgeDistribution expectedMale() {
		MaleAgeDistribution distribution = new MaleAgeDistribution();
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 5, 119));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 9, 83));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 15, 163));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 18, 90));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 24, 161));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 29, 93));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 44, 448));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 59, 530));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 64, 141));
		distribution.addItem(new AgeDistributionItem(Type.UNTIL, 74, 243));
		distribution.addItem(new AgeDistributionItem(Type.OVER, 75, 155));
		return distribution;
	}
	
	private EmploymentDistribution expectedEmployment() {
		EmploymentDistribution distribution = new EmploymentDistribution();
		distribution.addItem(new EmploymentDistributionItem(Employment.FULLTIME, 1456));
		distribution.addItem(new EmploymentDistributionItem(Employment.PARTTIME, 515));
		distribution.addItem(new EmploymentDistributionItem(Employment.NONE, 395));
		distribution.addItem(new EmploymentDistributionItem(Employment.STUDENT_TERTIARY, 97));
		distribution.addItem(new EmploymentDistributionItem(Employment.STUDENT_SECONDARY, 474));
		distribution.addItem(new EmploymentDistributionItem(Employment.STUDENT_PRIMARY, 174));
		distribution.addItem(new EmploymentDistributionItem(Employment.EDUCATION, 120));
		distribution.addItem(new EmploymentDistributionItem(Employment.RETIRED, 1035));
		distribution.addItem(new EmploymentDistributionItem(Employment.INFANT, 243));
		return distribution;
	}
}
