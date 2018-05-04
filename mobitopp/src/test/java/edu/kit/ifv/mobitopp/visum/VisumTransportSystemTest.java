package edu.kit.ifv.mobitopp.visum;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VisumTransportSystemTest {

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(VisumTransportSystem.class).usingGetClass().verify();
	}
}
