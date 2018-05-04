package edu.kit.ifv.mobitopp.data;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class AttractivitiesTest {

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(Attractivities.class).usingGetClass().verify();
	}
}
