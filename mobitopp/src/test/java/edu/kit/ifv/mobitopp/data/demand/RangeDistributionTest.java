package edu.kit.ifv.mobitopp.data.demand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RangeDistributionTest {

  private RangeDistributionIfc distribution;
  private int firstValue;
  private int secondValue;
  private int amount;
  private RangeDistributionItem firstItem;
  private RangeDistributionItem secondItem;

  @BeforeEach
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
	void hasItems() throws Exception {
  	RangeDistributionIfc emptyElements = distribution.createEmpty();
  	RangeDistribution emptyDistribution = new RangeDistribution();
		assertThat(emptyDistribution.hasItems()).isFalse();
		assertThat(emptyElements.hasItems()).isFalse();
		assertThat(distribution.hasItems()).isTrue();
	}

  @Test
  public void getsItemForValue() {
    RangeDistributionItem first = distribution.getItem(firstValue);
    RangeDistributionItem second = distribution.getItem(secondValue);

    assertThat(first).isEqualTo(firstItem);
    assertThat(second).isEqualTo(secondItem);
  }

  @Test
  public void getTotalAmount() {
    int totalAmount = distribution.getTotalAmount();
    
    assertThat(totalAmount).isEqualTo(firstItem.amount() + secondItem.amount());
  }
  
  @Test
  public void incrementsValue() {
    distribution.increment(firstValue);

    int amountAfterIncrement = amount + 1;
    assertThat(distribution.getItem(firstValue).amount()).isEqualTo(amountAfterIncrement);
    assertThat(distribution.getItem(secondValue)).isEqualTo(secondItem);
  }
  
  @Test
  public void amount() {
    assertThat(distribution.amount(firstValue)).isEqualTo(amount);
    assertThat(distribution.amount(secondValue)).isEqualTo(amount);
  }
  
  @Test
  public void createsEmptyDistribution() {
    RangeDistributionIfc empty = distribution.createEmpty();
    
    assertThat(empty.getItem(firstValue).amount()).isEqualTo(0);
    assertThat(empty.getItem(secondValue).amount()).isEqualTo(0);
  }
  
  @Test
	void incrementMissingValue() throws Exception {
		RangeDistribution distribution = new RangeDistribution();
		distribution.addItem(firstItem);
		
		RangeDistributionItem secondItem = distribution.getItem(secondValue);
		
		assertThat(secondItem).isEqualTo(new RangeDistributionItem(secondValue, 0));
	}
}
