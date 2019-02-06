package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

public class RangeDistributionTest {

  private RangeDistributionIfc distribution;
  private int firstValue;
  private int secondValue;
  private int amount;
  private RangeDistributionItem firstItem;
  private RangeDistributionItem secondItem;

  @Before
  public void initialise() {
    distribution = new RangeDistribution();
    firstValue = 1;
    secondValue = 2;
    amount = 2;
    firstItem = new RangeDistributionItem(firstValue, firstValue, amount);
    secondItem = new RangeDistributionItem(secondValue, secondValue, amount);
    distribution.addItem(firstItem);
    distribution.addItem(secondItem);
  }

  @Test
  public void getsItemForValue() {
    RangeDistributionItem first = distribution.getItem(firstValue);
    RangeDistributionItem second = distribution.getItem(secondValue);

    assertThat(first, is(equalTo(firstItem)));
    assertThat(second, is(equalTo(secondItem)));
  }

  @Test
  public void getTotalAmount() {
    int totalAmount = distribution.getTotalAmount();
    
    assertThat(totalAmount, is(equalTo(firstItem.amount() + secondItem.amount())));
  }
  
  @Test
  public void incrementsValue() {
    distribution.increment(firstValue);

    int amountAfterIncrement = amount + 1;
    assertThat(distribution.getItem(firstValue).amount(), is(equalTo(amountAfterIncrement)));
    assertThat(distribution.getItem(secondValue), is(equalTo(secondItem)));
  }
  
  @Test
  public void amount() {
    assertThat(distribution.amount(firstValue), is(equalTo(amount)));
    assertThat(distribution.amount(secondValue), is(equalTo(amount)));
  }
  
  @Test
  public void createsEmptyDistribution() {
    RangeDistributionIfc empty = distribution.createEmpty();
    
    assertThat(empty.getItem(firstValue).amount(), is(equalTo(0)));
    assertThat(empty.getItem(secondValue).amount(), is(equalTo(0)));
  }
}
