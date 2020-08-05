package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;
import edu.kit.ifv.mobitopp.data.local.ChargingType;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.visum.IdToOidMapper;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

@ExtendWith(MockitoExtension.class)
public class ZonesReaderCsvBasedTest {

  private static final int someZoneId = 1;
  private static final int anotherZoneId = 2;
  private static final int otherZoneId = 3;
  private static final int areaId = 1;
  private static final String someZoneName = "some name";
  private static final DefaultPower defaultPower = DefaultPower.zero;
	private static final int parkingFacilities = 1;

  private VisumNetwork network;
  private SimpleRoadNetwork roadNetwork;
  private AttractivitiesData attractivityData;
  private BikeSharingDataForZone bikeSharingData;
  private CarSharingDataForZone carSharingData;
  private ChargingDataBuilder chargingDataBuilder;
  private ChargingDataForZone chargingData;
  private AttractivitiesBuilder attractivitiesBuilder;
  private Attractivities attractivities;
  private ZoneLocationSelector locationSelector;
  private IdToOidMapper idToOid;
	private StructuralData structuralData;
	
	@Mock
	private BikeSharingDataRepository bikeSharingDataRepository;
	@Mock
	private CarSharingDataRepository carSharingDataRepository;
	@Mock
	private ParkingFacilityDataRepository parkingFacilitiesDataRepository;

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
    idToOid = IdToOidMapper.createFrom(map);
    roadNetwork = new SimpleRoadNetwork(network);
    structuralData = Example.attractivityData();
		attractivityData = new AttractivitiesData(structuralData);
		bikeSharingData = mock(BikeSharingDataForZone.class);
    carSharingData = mock(CarSharingDataForZone.class);
    chargingDataBuilder = mock(ChargingDataBuilder.class);
    chargingData = mock(ChargingDataForZone.class);
    attractivitiesBuilder = mock(AttractivitiesBuilder.class);
    attractivities = mock(Attractivities.class);
    locationSelector = mock(ZoneLocationSelector.class);

    when(chargingDataBuilder.chargingData(any(), any())).thenReturn(chargingData);
    when(attractivitiesBuilder.attractivities(anyString())).thenReturn(attractivities);
    when(locationSelector.selectLocation(any(), any())).thenReturn(dummyLocation());
    when(bikeSharingDataRepository.getData(any())).thenReturn(bikeSharingData);
    when(carSharingDataRepository.getData(any(), any(), any())).thenReturn(carSharingData);
    when(parkingFacilitiesDataRepository.getNumberOfParkingLots(any(), any())).thenReturn(parkingFacilities);
  }

  private Location dummyLocation() {
    return new Example().location();
  }

  @Test
  public void buildZone() {
    ZonesReaderCsvBased reader = newReader();

    List<Zone> zones = reader.getZones();

    Zone zone = zones.get(0);
		assertAll(() -> assertThat(zone.getId().getExternalId()).isEqualTo("" + someZoneId),
				() -> assertThat(zone.getName()).isEqualTo(someZoneName),
				() -> assertThat(zone.getAreaType()).isEqualTo(ZoneAreaType.CITYOUTSKIRT),
				() -> assertThat(zone.getRegionType()).isEqualTo(new DefaultRegionType(1)),
				() -> assertThat(zone.getClassification()).isEqualTo(ZoneClassificationType.studyArea),
				() -> assertThat(zone.bikeSharing()).isEqualTo(bikeSharingData),
				() -> assertThat(zone.carSharing()).isEqualTo(carSharingData),
				() -> assertThat(zone.charging()).isEqualTo(chargingData),
				() -> assertThat(zone.attractivities()).isEqualTo(attractivities),
				() -> assertThat(zone.getNumberOfParkingPlaces()).isEqualTo(parkingFacilities),
				() -> assertThat(zone.centroidLocation()).isEqualTo(dummyLocation()));
  }

	private ZonesReaderCsvBased newReader() {
		ChargingType charging = ChargingType.limited;
		ZonePropertiesData zoneProperties = new ZonePropertiesData(structuralData,
				Example.areaTypeRepository());
		return new ZonesReaderCsvBased(network, roadNetwork, zoneProperties, attractivityData,
				parkingFacilitiesDataRepository, bikeSharingDataRepository, carSharingDataRepository, charging, defaultPower,
				idToOid) {

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
