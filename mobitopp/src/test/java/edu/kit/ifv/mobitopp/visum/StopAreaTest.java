package edu.kit.ifv.mobitopp.visum;

import org.junit.Test;

import edu.kit.ifv.mobitopp.visum.StopArea;
import nl.jqno.equalsverifier.EqualsVerifier;

public class StopAreaTest {

	@Test
	public void equalsAndHashCode() throws Exception {

		EqualsVerifier.forClass(StopArea.class).usingGetClass().verify();
	}
}
