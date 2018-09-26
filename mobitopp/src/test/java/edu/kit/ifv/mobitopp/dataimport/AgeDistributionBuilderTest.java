package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;

public class AgeDistributionBuilderTest {

	private StructuralData demographyData;
	private AgeDistributionBuilder builder;

	@Before
	public void initialise() {
		demographyData = Example.demographyData();
		builder = new AgeDistributionBuilder(demographyData);
	}

	@Test
	public void buildFemaleAgeDistribution() {
		FemaleAgeDistribution distribution = builder.buildFemale();

		assertThat(distribution, is(equalTo(expectedFemale())));
	}

	@Test
	public void buildMaleAgeDistribution() {
		MaleAgeDistribution distribution = builder.buildMale();

		assertThat(distribution, is(equalTo(expectedMale())));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void verifiesContinuousDistributionUntilTheEnd() {
		StructuralData missingAgeGroup = Example.missingAgeGroup();
		AgeDistributionBuilder builder = new AgeDistributionBuilder(missingAgeGroup);
		builder.buildFemale();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void verifiesContinuousDistributionAtTheBeginning() {
		StructuralData missingAgeGroup = Example.missingAgeGroup();
		AgeDistributionBuilder builder = new AgeDistributionBuilder(missingAgeGroup);
		builder.buildMale();
	}

	private FemaleAgeDistribution expectedFemale() {
		FemaleAgeDistribution distribution = new FemaleAgeDistribution();
		distribution.addItem(new AgeDistributionItem(0, 5, 113));
		distribution.addItem(new AgeDistributionItem(6, 9, 79));
		distribution.addItem(new AgeDistributionItem(10, 15, 155));
		distribution.addItem(new AgeDistributionItem(16, 18, 85));
		distribution.addItem(new AgeDistributionItem(19, 24, 155));
		distribution.addItem(new AgeDistributionItem(25, 29, 92));
		distribution.addItem(new AgeDistributionItem(30, 44, 445));
		distribution.addItem(new AgeDistributionItem(45, 59, 516));
		distribution.addItem(new AgeDistributionItem(60, 64, 137));
		distribution.addItem(new AgeDistributionItem(65, 74, 308));
		distribution.addItem(new AgeDistributionItem(75, Integer.MAX_VALUE, 198));
		return distribution;
	}

	private MaleAgeDistribution expectedMale() {
		MaleAgeDistribution distribution = new MaleAgeDistribution();
		distribution.addItem(new AgeDistributionItem(0, 5, 119));
		distribution.addItem(new AgeDistributionItem(6, 9, 83));
		distribution.addItem(new AgeDistributionItem(10, 15, 163));
		distribution.addItem(new AgeDistributionItem(16, 18, 90));
		distribution.addItem(new AgeDistributionItem(19, 24, 161));
		distribution.addItem(new AgeDistributionItem(25, 29, 93));
		distribution.addItem(new AgeDistributionItem(30, 44, 448));
		distribution.addItem(new AgeDistributionItem(45, 59, 530));
		distribution.addItem(new AgeDistributionItem(60, 64, 141));
		distribution.addItem(new AgeDistributionItem(65, 74, 243));
		distribution.addItem(new AgeDistributionItem(75, Integer.MAX_VALUE, 155));
		return distribution;
	}
}
