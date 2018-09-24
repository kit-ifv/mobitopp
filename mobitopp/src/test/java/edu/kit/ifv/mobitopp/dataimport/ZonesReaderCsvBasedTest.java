package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;
import edu.kit.ifv.mobitopp.data.local.ChargingType;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ZonesReaderCsvBasedTest {

	private static final int someZoneId = 1;
	private static final int anotherZoneId = 2;
	private static final int otherZoneId = 3;
	private static final int areaId = 1;
	private static final String someZoneName = "some name";
	private static final DefaultPower defaultPower = DefaultPower.zero;

	private VisumNetwork network;
	private SimpleRoadNetwork roadNetwork;
	private StructuralData attractivityData;
	private CarSharingBuilder carSharingBuilder;
	private CarSharingDataForZone carSharingData;
	private ChargingDataBuilder chargingDataBuilder;
	private ChargingDataForZone chargingData;
	private AttractivitiesBuilder attractivitiesBuilder;
	private Attractivities attractivities;
	private ZoneLocationSelector locationSelector;

	@Before
	public void initialise() {
		VisumSurface area = visumSurface().withId(areaId).build();
		VisumZone someZone = visumZone()
				.withId(someZoneId)
				.withName(someZoneName)
				.withArea(areaId)
				.build();
		VisumZone anotherZone = visumZone().withId(anotherZoneId).withArea(areaId).build();
		VisumZone otherZone = visumZone().withId(otherZoneId).withArea(areaId).build();
		network = visumNetwork()
				.withDefaultCarSystem()
				.with(area)
				.with(someZone)
				.with(anotherZone)
				.with(otherZone)
				.build();
		roadNetwork = new SimpleRoadNetwork(network);
		attractivityData = Example.attractivityData();
		carSharingBuilder = mock(CarSharingBuilder.class);
		carSharingData = mock(CarSharingDataForZone.class);
		chargingDataBuilder = mock(ChargingDataBuilder.class);
		chargingData = mock(ChargingDataForZone.class);
		attractivitiesBuilder = mock(AttractivitiesBuilder.class);
		attractivities = mock(Attractivities.class);
		locationSelector = mock(ZoneLocationSelector.class);

		when(carSharingBuilder.carsharingIn(any(), any(), any())).thenReturn(carSharingData);
		when(chargingDataBuilder.chargingData(any(), any())).thenReturn(chargingData);
		when(attractivitiesBuilder.attractivities()).thenReturn(attractivities);
		when(locationSelector.selectLocation(eq(someZone), any())).thenReturn(dummyLocation());
	}

	private Location dummyLocation() {
		return new Example().location();
	}

	@Test
	public void buildZone() {
		ZonesReaderCsvBased reader = newReader();

		List<Zone> zones = reader.getZones();

		Zone zone = zones.get(0);
		assertThat(zone.getId(), is(equalTo("Z" + someZoneId)));
		assertThat(zone.getName(), is(equalTo(someZoneName)));
		assertThat(zone.getAreaType(), is(equalTo(ZoneAreaType.CITYOUTSKIRT)));
		assertThat(zone.getClassification(), is(equalTo(ZoneClassificationType.areaOfInvestigation)));
		assertThat(zone.carSharing(), is(equalTo(carSharingData)));
		assertThat(zone.charging(), is(equalTo(chargingData)));
		assertThat(zone.attractivities(), is(equalTo(attractivities)));
		assertThat(zone.centroidLocation(), is(equalTo(dummyLocation())));
	}

	private ZonesReaderCsvBased newReader() {
		ChargingType charging = ChargingType.limited;
		return new ZonesReaderCsvBased(network, roadNetwork, attractivityData, charging, defaultPower) {

			@Override
			CarSharingBuilder carSharingBuilder() {
				return carSharingBuilder;
			}

			@Override
			ChargingDataBuilder chargingDataBuilder() {
				return chargingDataBuilder;
			}

			@Override
			AttractivitiesBuilder attractivitiesBuilder() {
				return attractivitiesBuilder;
			}

			@Override
			ZoneLocationSelector locationSelector() {
				return locationSelector;
			}
		};
	}
}
