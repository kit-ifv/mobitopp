package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;

public class SingleZoneTest {

	@Test
	void idIsEqualToZoneId() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		SingleZone singleZone = new SingleZone(someZone);
		
		assertThat(singleZone.getExternalId()).isEqualTo(someZone.getId().getExternalId());
	}
	
	@Test
	void contains() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		DemandZone otherZone = ExampleDemandZones.create().getOtherZone();
		SingleZone singleZone = new SingleZone(someZone);

		assertAll(
				() -> assertTrue(singleZone.contains(someZone.getId())),
				() -> assertFalse(singleZone.contains(otherZone.getId())));
	}
}
