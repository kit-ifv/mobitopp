package edu.kit.ifv.mobitopp.visum;

import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper;
import nl.jqno.equalsverifier.EqualsVerifier;

public class VisumLinkTypeTest {

	@Test
	public void equalsAndHashCode() {
		VisumTransportSystemSet someSet = TransportSystemHelper.dummySet();
		VisumTransportSystemSet otherSet = TransportSystemHelper.otherSet();
		EqualsVerifier
				.forClass(VisumLinkType.class)
				.withPrefabValues(VisumTransportSystemSet.class, someSet, otherSet)
				.usingGetClass()
				.verify();
	}
}
