package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;
import edu.kit.ifv.mobitopp.data.local.ChargingType;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.visum.IdToOidMapper;
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
  private AttractivitiesData attractivityData;
  private CarSharingBuilder carSharingBuilder;
  private CarSharingDataForZone carSharingData;
  private ChargingDataBuilder chargingDataBuilder;
  private ChargingDataForZone chargingData;
  private AttractivitiesBuilder attractivitiesBuilder;
  private Attractivities attractivities;
  private ZoneLocationSelector locationSelector;
  private IdToOidMapper idToOid;

  @BeforeEach
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
    Map<String, Integer> map = Stream
        .of(someZoneId, anotherZoneId, otherZoneId)
        .collect(toMap(z -> String.valueOf(z), z -> z - 1));
    idToOid = map::get;
    roadNetwork = new SimpleRoadNetwork(network);
    attractivityData = new AttractivitiesData(Example.attractivityData(),
        Example.areaTypeRepository());
    carSharingBuilder = mock(CarSharingBuilder.class);
    carSharingData = mock(CarSharingDataForZone.class);
    chargingDataBuilder = mock(ChargingDataBuilder.class);
    chargingData = mock(ChargingDataForZone.class);
    attractivitiesBuilder = mock(AttractivitiesBuilder.class);
    attractivities = mock(Attractivities.class);
    locationSelector = mock(ZoneLocationSelector.class);

    when(carSharingBuilder.carsharingIn(any(), any(), any())).thenReturn(carSharingData);
    when(chargingDataBuilder.chargingData(any(), any())).thenReturn(chargingData);
    when(attractivitiesBuilder.attractivities(anyString())).thenReturn(attractivities);
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
    assertAll(() -> assertThat(zone.getId().getExternalId(), is(equalTo("" + someZoneId))),
        () -> assertThat(zone.getName(), is(equalTo(someZoneName))),
        () -> assertThat(zone.getAreaType(), is(equalTo(ZoneAreaType.CITYOUTSKIRT))),
        () -> assertThat(zone.getRegionType(), is(equalTo(new DefaultRegionType(1)))),
        () -> assertThat(zone.getClassification(),
            is(equalTo(ZoneClassificationType.studyArea))),
        () -> assertThat(zone.carSharing(), is(equalTo(carSharingData))),
        () -> assertThat(zone.charging(), is(equalTo(chargingData))),
        () -> assertThat(zone.attractivities(), is(equalTo(attractivities))),
        () -> assertThat(zone.centroidLocation(), is(equalTo(dummyLocation()))));
  }

  private ZonesReaderCsvBased newReader() {
    ChargingType charging = ChargingType.limited;
    return new ZonesReaderCsvBased(network, roadNetwork, attractivityData, charging, defaultPower,
        idToOid) {

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
