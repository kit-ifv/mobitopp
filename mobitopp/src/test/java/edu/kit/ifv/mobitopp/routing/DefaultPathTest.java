package edu.kit.ifv.mobitopp.routing;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class DefaultPathTest {

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(DefaultPath.class).usingGetClass().verify();
	}
}
