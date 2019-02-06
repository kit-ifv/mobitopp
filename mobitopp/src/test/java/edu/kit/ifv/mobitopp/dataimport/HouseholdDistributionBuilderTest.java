package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class HouseholdDistributionBuilderTest {

  private StructuralData demographyData;
  private HouseholdDistributionBuilder builder;

  @Before
  public void initialise() {
    demographyData = Example.demographyData();
    builder = new HouseholdDistributionBuilder(demographyData, StandardAttribute.householdSize);
  }

  @Test
  public void build() {
    String zoneId = "1";
    RangeDistributionIfc distribution = builder.build(zoneId);

    assertThat(distribution, is(equalTo(expectedHouseholds())));
  }

  private RangeDistributionIfc expectedHouseholds() {
    RangeDistributionIfc distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(1, 161));
    distribution.addItem(new RangeDistributionItem(2, 508));
    distribution.addItem(new RangeDistributionItem(3, 29));
    distribution.addItem(new RangeDistributionItem(4, 16));
    distribution.addItem(new RangeDistributionItem(5, 402));
    distribution.addItem(new RangeDistributionItem(6, 287));
    distribution.addItem(new RangeDistributionItem(7, 4));
    distribution.addItem(new RangeDistributionItem(8, 108));
    distribution.addItem(new RangeDistributionItem(9, 169));
    distribution.addItem(new RangeDistributionItem(10, 6));
    distribution.addItem(new RangeDistributionItem(11, 122));
    distribution.addItem(new RangeDistributionItem(12, 233));
    return distribution;
  }
}
