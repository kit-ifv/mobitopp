package edu.kit.ifv.mobitopp.data;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;

import edu.kit.ifv.mobitopp.simulation.emobility.NoChargingDataForZone;

public class MockedZones {

	private final Zone someZone;
	private final Zone otherZone;

	public MockedZones(Zone someZone, Zone otherZone) {
		super();
		this.someZone = someZone;
		this.otherZone = otherZone;
	}

	public static MockedZones create() {
		Zone someZone = mock(Zone.class);
		when(someZone.getId()).thenReturn(new ZoneId("1", 1));
		when(someZone.charging()).thenReturn(NoChargingDataForZone.noCharging);
		Zone otherZone = mock(Zone.class);
		when(otherZone.getId()).thenReturn(new ZoneId("2", 2));
		when(otherZone.charging()).thenReturn(NoChargingDataForZone.noCharging);

		return new MockedZones(someZone, otherZone);
	}

	public void verify() {
		verifyOnlyCallsToGetId(someZone);
		verifyOnlyCallsToGetId(otherZone);
	}

	private void verifyOnlyCallsToGetId(Zone zone) {
		verifyPossibleCallOn(zone).getId();
		verifyPossibleCallOn(zone).charging();
		verifyNoMoreInteractions(zone);
	}

	private Zone verifyPossibleCallOn(Zone zone) {
		return Mockito.verify(zone, atLeast(0));
	}

	public Zone someZone() {
		return someZone;
	}

	public Zone otherZone() {
		return otherZone;
	}

}
