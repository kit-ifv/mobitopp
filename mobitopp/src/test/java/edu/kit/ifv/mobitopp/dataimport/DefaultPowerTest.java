package edu.kit.ifv.mobitopp.dataimport;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class DefaultPowerTest {

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier
				.forClass(DefaultPower.class)
				.withIgnoredFields("CHARGING_POWER_KW_PUBLIC", "CHARGING_POWER_KW_SEMIPUBLIC",
						"CHARGING_POWER_KW_PRIVATE")
				.usingGetClass()
				.verify();
	}
}
