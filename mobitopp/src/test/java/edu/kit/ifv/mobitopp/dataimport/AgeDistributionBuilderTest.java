package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.AgeDistributionItem.Type;
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
}
