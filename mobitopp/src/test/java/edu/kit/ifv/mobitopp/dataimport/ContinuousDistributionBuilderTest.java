package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.ContinuousDistribution;
import edu.kit.ifv.mobitopp.data.demand.ContinuousDistributionItem;

public class ContinuousDistributionBuilderTest {

	private static final String prefix = "age_f";
  private StructuralData demographyData;
	private ContinuousDistributionBuilder builder;

	@Before
	public void initialise() {
		demographyData = Example.demographyData();
		builder = new ContinuousDistributionBuilder(demographyData, prefix);
	}

	@Test
	public void buildContinuousDistribution() {
    String zoneId = "1";
		ContinuousDistribution distribution = builder.buildFor(zoneId, ContinuousDistribution::new);

		assertThat(distribution, is(equalTo(expectedDistribution())));
	}

	@Test(expected=IllegalArgumentException.class)
	public void verifiesContinuousDistributionUntilTheEnd() {
		StructuralData missingAgeGroup = Example.missingAgeGroup();
    String zoneId = "1";
		ContinuousDistributionBuilder builder = new ContinuousDistributionBuilder(missingAgeGroup, prefix);
		builder.buildFor(zoneId, ContinuousDistribution::new);
	}
	
	private ContinuousDistribution expectedDistribution() {
		ContinuousDistribution distribution = new ContinuousDistribution();
		distribution.addItem(new ContinuousDistributionItem(0, 5, 113));
		distribution.addItem(new ContinuousDistributionItem(6, 9, 79));
		distribution.addItem(new ContinuousDistributionItem(10, 15, 155));
		distribution.addItem(new ContinuousDistributionItem(16, 18, 85));
		distribution.addItem(new ContinuousDistributionItem(19, 24, 155));
		distribution.addItem(new ContinuousDistributionItem(25, 29, 92));
		distribution.addItem(new ContinuousDistributionItem(30, 44, 445));
		distribution.addItem(new ContinuousDistributionItem(45, 59, 516));
		distribution.addItem(new ContinuousDistributionItem(60, 64, 137));
		distribution.addItem(new ContinuousDistributionItem(65, 74, 308));
		distribution.addItem(new ContinuousDistributionItem(75, Integer.MAX_VALUE, 198));
		return distribution;
	}
}
