package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

public class HouseholdDistributionTest {

  private static final int someHousehold = 0;
  private static final int anotherHousehodl = 1;
  private HouseholdDistribution distribution;

  @Before
  public void initialise() {
    distribution = HouseholdDistribution.createDefault();
    distribution.getItem(someHousehold).increment();
    distribution.getItem(anotherHousehodl).increment();
  }
  
  @Test
  public void createsEmptyDistribution() {
    HouseholdDistribution empty = distribution.createEmpty();
    
    assertThat(empty.getItem(someHousehold).amount(), is(equalTo(0)));
    assertThat(empty.getItem(anotherHousehodl).amount(), is(equalTo(0)));
  }
}
