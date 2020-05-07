package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;

public class MultipleZonesTest {

	@Test
	void containsZone() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		DemandZone otherZone = ExampleDemandZones.create().getOtherZone();
		ZoneId notAvailableZone = new ZoneId("undefined", otherZone.getId().getMatrixColumn() + 1);
		MultipleZones multipleZones = new MultipleZones("1", someZone, otherZone);

		assertAll(() -> assertTrue(multipleZones.contains(someZone.getId())),
				() -> assertTrue(multipleZones.contains(otherZone.getId())),
				() -> assertFalse(multipleZones.contains(notAvailableZone)));
	}
	
	@Test
	void canBeEmpty() throws Exception {
		MultipleZones zones = new MultipleZones("1");
		
		assertThat(zones.getZones(), is(empty()));
	}
}
