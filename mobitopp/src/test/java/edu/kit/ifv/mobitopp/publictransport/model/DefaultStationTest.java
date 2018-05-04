package edu.kit.ifv.mobitopp.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class DefaultStationTest {

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier
				.forClass(DefaultStation.class)
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.withOnlyTheseFields("id")
				.usingGetClass()
				.verify();
	}
}
