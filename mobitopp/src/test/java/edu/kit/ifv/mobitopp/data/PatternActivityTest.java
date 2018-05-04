package edu.kit.ifv.mobitopp.data;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class PatternActivityTest {

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(PatternActivity.class).usingGetClass().verify();
	}
}
