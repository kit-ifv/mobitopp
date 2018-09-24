package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.ChargingDataFactory;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;

public class CreateMissingChargingDataTest {

	private static final int oid = 1;

	private ChargingDataFactory factory;

	@Before
	public void initialise() {
		factory = mock(ChargingDataFactory.class);
	}

	@Test
	public void resolvesExistingData() {
		ChargingDataForZone someData = mock(ChargingDataForZone.class);
		
		executeAndVerify(someData, singletonMap(oid, someData));
	}

	@Test
	public void createsEmptyForMissingData() {
		ChargingDataForZone emptyData = mock(ChargingDataForZone.class);
		when(factory.create(emptyList())).thenReturn(emptyData);
		
		executeAndVerify(emptyData, emptyMap());
	}

	private void executeAndVerify(
			ChargingDataForZone expectedData, Map<Integer, ChargingDataForZone> mapping) {
		CreateMissingChargingData resolve = new CreateMissingChargingData(mapping, factory);

		ChargingDataForZone resolved = resolve.chargingDataFor(oid);

		assertThat(resolved, is(sameInstance(expectedData)));
	}
}
