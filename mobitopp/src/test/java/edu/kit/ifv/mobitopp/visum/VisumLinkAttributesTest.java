package edu.kit.ifv.mobitopp.visum;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VisumLinkAttributesTest {

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(VisumLinkAttributes.class).usingGetClass().verify();
	}
}
