package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;

public class MultipleRegionsTest {

	@Test
	void containsZone() throws Exception {
		DemandRegion somePart = mock(DemandRegion.class);
		DemandRegion otherPart = mock(DemandRegion.class);
		ZoneId containedZone = new ZoneId("1", 0);
		ZoneId missingZone = new ZoneId("2", 1);
		when(somePart.contains(containedZone)).thenReturn(false);
		when(somePart.contains(missingZone)).thenReturn(false);
		when(otherPart.contains(containedZone)).thenReturn(true);
		when(otherPart.contains(missingZone)).thenReturn(false);
		
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		Demography someDemography = someZone.nominalDemography();
		MultipleRegions regions = new MultipleRegions("1", RegionalLevel.community, someDemography,
				somePart, otherPart);

		assertAll(() -> assertTrue(regions.contains(containedZone)),
				() -> assertFalse(regions.contains(missingZone)));
		
		verify(somePart).contains(containedZone);
		verify(somePart).contains(missingZone);
		verify(otherPart).contains(containedZone);
		verify(otherPart).contains(missingZone);
	}

	@Test
	void canBeEmpty() throws Exception {
		Demography someDemography = ExampleDemandZones.create().getSomeZone().nominalDemography();
		MultipleRegions regions = new MultipleRegions("1", RegionalLevel.community, someDemography);

		assertThat(regions.zones()).isEmpty();
	}
}
