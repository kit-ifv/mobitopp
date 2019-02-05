package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.Employment;

public class EmploymentDistributionTest {

  private EmploymentDistribution distribution;

  @Before
  public void initialise() {
    distribution = EmploymentDistribution.createDefault();
    distribution.getItem(Employment.FULLTIME).increment();
    distribution.getItem(Employment.NONE).increment();
  }
  
  @Test
  public void createsEmptyDistribution() {
    EmploymentDistribution empty = distribution.createEmpty();
    
    assertThat(empty.getItem(Employment.FULLTIME).amount(), is(equalTo(0)));
    assertThat(empty.getItem(Employment.NONE).amount(), is(equalTo(0)));
  }
}
