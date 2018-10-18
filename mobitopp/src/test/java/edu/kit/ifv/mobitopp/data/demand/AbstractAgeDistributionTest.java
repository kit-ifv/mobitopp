package edu.kit.ifv.mobitopp.data.demand;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class AbstractAgeDistributionTest {

	@Test
	public void getsItemForAge() {
		FemaleAgeDistribution distribution = new FemaleAgeDistribution();
		int firstAge = 1;
		int secondAge = 2;
		int amount = 2;
		AgeDistributionItem firstItem = new AgeDistributionItem(firstAge, firstAge, amount);
		AgeDistributionItem secondItem = new AgeDistributionItem(secondAge, secondAge, amount);
		distribution.addItem(firstItem);
		distribution.addItem(secondItem);
		
		AgeDistributionItem first = distribution.getItem(firstAge);
		AgeDistributionItem second = distribution.getItem(secondAge);
		
		assertThat(first, is(equalTo(firstItem)));
		assertThat(second, is(equalTo(secondItem)));
	}
}
