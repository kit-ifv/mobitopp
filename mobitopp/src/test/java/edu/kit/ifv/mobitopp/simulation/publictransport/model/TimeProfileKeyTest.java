package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.publictransport.model.TimeProfileKey;
import nl.jqno.equalsverifier.EqualsVerifier;

public class TimeProfileKeyTest {

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(TimeProfileKey.class).usingGetClass().verify();
	}

}
