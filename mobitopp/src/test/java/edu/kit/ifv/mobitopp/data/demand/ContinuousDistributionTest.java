package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

public class ContinuousDistributionTest {

  private ContinuousDistribution distribution;
  private int firstValue;
  private int secondValue;
  private int amount;
  private ContinuousDistributionItem firstItem;
  private ContinuousDistributionItem secondItem;

  @Before
  public void initialise() {
    distribution = new ContinuousDistribution();
    firstValue = 1;
    secondValue = 2;
    amount = 2;
    firstItem = new ContinuousDistributionItem(firstValue, firstValue, amount);
    secondItem = new ContinuousDistributionItem(secondValue, secondValue, amount);
    distribution.addItem(firstItem);
    distribution.addItem(secondItem);
  }

  @Test
  public void getsItemForValue() {
    ContinuousDistributionItem first = distribution.getItem(firstValue);
    ContinuousDistributionItem second = distribution.getItem(secondValue);

    assertThat(first, is(equalTo(firstItem)));
    assertThat(second, is(equalTo(secondItem)));
  }

  @Test
  public void getTotalAmount() {
    int totalAmount = distribution.getTotalAmount();
    
    assertThat(totalAmount, is(equalTo(firstItem.amount() + secondItem.amount())));
  }
}
