package edu.kit.ifv.mobitopp.simulation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;

public class FilterFractionOfHouseholdsTest {

	private HouseholdForSetup household;

	@BeforeEach
	public void beforeEach() {
		household = mock(HouseholdForSetup.class);
	}

	@Test
	void acceptAll() throws Exception {
		FilterFractionOfHouseholds filter = new FilterFractionOfHouseholds(1.0f);

		assertTrue(filter.test(household));
	}

	@Test
	void rejectAll() throws Exception {
		FilterFractionOfHouseholds filter = new FilterFractionOfHouseholds(0.0f);

		assertFalse(filter.test(household));
	}

	@Test
	void acceptEveryOther() throws Exception {
		FilterFractionOfHouseholds filter = new FilterFractionOfHouseholds(0.5f);

		assertAll(
				() -> assertFalse(filter.test(household)), 
				() -> assertTrue(filter.test(household)),
				() -> assertFalse(filter.test(household)), 
				() -> assertTrue(filter.test(household)));
	}
}
