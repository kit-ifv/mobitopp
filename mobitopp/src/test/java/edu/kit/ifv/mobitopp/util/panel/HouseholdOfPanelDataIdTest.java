package edu.kit.ifv.mobitopp.util.panel;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class HouseholdOfPanelDataIdTest {

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(HouseholdOfPanelDataId.class)
				.withIgnoredFields("weight")
				.usingGetClass()
				.verify();
	}
}
