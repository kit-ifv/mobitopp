package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class RangeDistributionBuilderTest {

	private static final AttributeType type = StandardAttribute.femaleAge;
  private StructuralData demographyData;
	private RangeDistributionBuilder builder;

	@Before
	public void initialise() {
		demographyData = Example.demographyData();
		builder = new RangeDistributionBuilder(demographyData, type);
	}

	@Test
	public void buildContinuousDistribution() {
    String zoneId = "1";
		RangeDistribution distribution = builder.buildFor(zoneId, RangeDistribution::new);

		assertThat(distribution, is(equalTo(expectedDistribution())));
	}

	@Test(expected=IllegalArgumentException.class)
	public void verifiesContinuousDistributionUntilTheEnd() {
		StructuralData missingAgeGroup = Example.missingAgeGroup();
    String zoneId = "1";
		RangeDistributionBuilder builder = new RangeDistributionBuilder(missingAgeGroup, type);
		builder.buildFor(zoneId, RangeDistribution::new);
	}
	
	private RangeDistribution expectedDistribution() {
		RangeDistribution distribution = new RangeDistribution();
		distribution.addItem(new RangeDistributionItem(0, 5, 113));
		distribution.addItem(new RangeDistributionItem(6, 9, 79));
		distribution.addItem(new RangeDistributionItem(10, 15, 155));
		distribution.addItem(new RangeDistributionItem(16, 18, 85));
		distribution.addItem(new RangeDistributionItem(19, 24, 155));
		distribution.addItem(new RangeDistributionItem(25, 29, 92));
		distribution.addItem(new RangeDistributionItem(30, 44, 445));
		distribution.addItem(new RangeDistributionItem(45, 59, 516));
		distribution.addItem(new RangeDistributionItem(60, 64, 137));
		distribution.addItem(new RangeDistributionItem(65, 74, 308));
		distribution.addItem(new RangeDistributionItem(75, Integer.MAX_VALUE, 198));
		return distribution;
	}
}
