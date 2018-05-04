package edu.kit.ifv.mobitopp.simulation.carsharing;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class BaseCarSharingOrganizationTest {

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(BaseCarSharingOrganization.class).usingGetClass().verify();
	}
}
