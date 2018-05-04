package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.LimitedChargingDataForZone;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ChargingDataBuilderTest {

	private static final DefaultPower defaultPower = DefaultPower.zero;
	private VisumNetwork visumNetwork;
	private ZoneLocationSelector locationSelector;
	private VisumZone zone;
	private ZonePolygon polygon;

	@Before
	public void initialise() {
		visumNetwork = visumNetwork().build();
		locationSelector = mock(ZoneLocationSelector.class);
		zone = visumZone().build();
		polygon = new Example().polygonAcceptingPoints();
		when(locationSelector.selectLocation(eq(zone), any())).thenReturn(new Example().location());
	}

	@Test
	public void usesFactory() {
		ChargingDataFactory factory = mock(ChargingDataFactory.class);
		when(factory.create(any())).thenAnswer(
				facilities -> new LimitedChargingDataForZone(facilities.getArgument(0), defaultPower));
		ChargingDataBuilder builder = new ChargingDataBuilder(visumNetwork, locationSelector, factory,
				defaultPower);

		ChargingDataForZone chargingData = builder.chargingData(zone, polygon);

		assertThat(chargingData, is(instanceOf(LimitedChargingDataForZone.class)));
		verify(factory).create(any());
	}
}
