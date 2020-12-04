package edu.kit.ifv.mobitopp.publictransport.model;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TransportSystemTest {

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(TransportSystem.class).usingGetClass().verify();
	}
}
