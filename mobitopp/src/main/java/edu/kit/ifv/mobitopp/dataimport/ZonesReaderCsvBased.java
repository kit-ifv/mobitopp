package edu.kit.ifv.mobitopp.dataimport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.MaasDataForZone;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.local.ChargingType;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.IdToOidMapper;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ZonesReaderCsvBased implements ZonesReader {

  private final VisumNetwork visumNetwork;
  private final ChargingDataBuilder chargingDataBuilder;
  private final ZonePropertiesData zoneproperties;
  private final AttractivitiesData attractivities;

  private final ZoneLocationSelector locationSelector;
  private final IdToOidMapper idToOid;
  private final ParkingFacilityDataRepository parkingFacilitiesDataRepository;
	private final CarSharingDataRepository carSharingDataRepository;

  ZonesReaderCsvBased(
      VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, ZonePropertiesData zoneproperties, AttractivitiesData attractivities, 
      ParkingFacilityDataRepository parkingFacilitiesDataRepository, CarSharingDataRepository carSharingDataRepository, ChargingType charging, DefaultPower defaultPower, IdToOidMapper mapper) {
    super();
    this.visumNetwork = visumNetwork;
    this.zoneproperties = zoneproperties;
    this.attractivities = attractivities;
    this.parkingFacilitiesDataRepository = parkingFacilitiesDataRepository;
    this.carSharingDataRepository = carSharingDataRepository;
    this.idToOid = mapper;
    ChargingDataFactory factory = charging.factory(defaultPower);
    locationSelector = new ZoneLocationSelector(roadNetwork);
    chargingDataBuilder = new ChargingDataBuilder(visumNetwork, locationSelector, factory,
        defaultPower);
  }

	@Override
	public List<Zone> getZones() {
		zoneproperties.data().resetIndex();
		ArrayList<VisumZone> visumZones = new ArrayList<>(visumNetwork.zones.values());
		Collections.sort(visumZones, Comparator.comparing(zone -> zone.id));
		List<Zone> zones = new ArrayList<>();
		while (zoneproperties.data().hasNext()) {
			VisumZone visumZone = visumNetwork.zones.get(zoneproperties.data().currentZone());
			zones.add(zoneFrom(visumZone));
			System.out
					.println(
							String.format("Processed zone %1d of %2d zones", visumZone.id, visumZones.size()));
			zoneproperties.data().next();
		}
		return zones;
	}

  private Zone zoneFrom(VisumZone visumZone) {
    String visumId = String.valueOf(visumZone.id);
    int oid = idToOid.map(visumId);
    ZoneId zoneId = new ZoneId(visumId, oid);
    String name = visumZone.name;
    AreaType areaType = currentZoneAreaType(visumId);
    RegionType regionType = regionType(visumId);
    ZoneClassificationType classification = currentClassification(visumId);
    int parkingPlaces = getParkingPlaces(visumZone, zoneId);
    ZonePolygon polygon = currentZonePolygon(visumZone);
    Location centroid = polygon.centroidLocation();
    Attractivities attractivities = attractivities(visumId);
    ChargingDataForZone chargingData = chargingData(visumZone, polygon);
    Zone zone = new Zone(zoneId, name, areaType, regionType, classification, parkingPlaces,
        centroid, attractivities, chargingData);
    CarSharingDataForZone carSharingData = getCarSharingData(visumZone, polygon, zone);
    zone.setCarSharing(carSharingData);
    zone.setMaas(MaasDataForZone.everywhereAvailable());
    return zone;
  }

	private CarSharingDataForZone getCarSharingData(
			VisumZone visumZone, ZonePolygon polygon, Zone zone) {
		return carSharingDataRepository.getData(visumZone, polygon, zone);
	}

	private int getParkingPlaces(VisumZone visumZone, ZoneId zoneId) {
		return parkingFacilitiesDataRepository.getNumberOfParkingLots(visumZone, zoneId);
	}

  private ChargingDataForZone chargingData(VisumZone visumZone, ZonePolygon polygon) {
    return chargingDataBuilder().chargingData(visumZone, polygon);
  }

  ChargingDataBuilder chargingDataBuilder() {
    return chargingDataBuilder;
  }

  private Attractivities attractivities(String zoneId) {
    return attractivitiesBuilder().attractivities(zoneId);
  }

  AttractivitiesBuilder attractivitiesBuilder() {
    return new AttractivitiesBuilder(attractivities);
  }

  private ZonePolygon currentZonePolygon(VisumZone visumZone) {
    Location centroid = makeLocation(visumZone, visumZone.coord);
    return new ZonePolygon(surface(visumZone), centroid);
  }

  private Location makeLocation(VisumZone zone, VisumPoint2 coordinate) {
    return locationSelector().selectLocation(zone, coordinate);
  }

  ZoneLocationSelector locationSelector() {
    return locationSelector;
  }

  private VisumSurface surface(VisumZone visumZone) {
    return visumNetwork.areas.get(visumZone.areaId);
  }

  private ZoneClassificationType currentClassification(String zoneId) {
    return zoneproperties.currentClassification(zoneId);
  }

  private RegionType regionType(String zoneId) {
    return zoneproperties.currentRegionType(zoneId);
  }
  
  private AreaType currentZoneAreaType(String zoneId) {
    return zoneproperties.currentZoneAreaType(zoneId);
  }

  public static ZonesReaderCsvBased from(
      VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, ChargingType charging,
      DefaultPower defaultPower, File zonePropertiesDataFile, File attractivityDataFile, File parkingFacilitiesDataFile,
      File carSharingStationsDataFile, AreaTypeRepository areaTypeRepository, IdToOidMapper mapper) {
  	ZonePropertiesData zonePropertiesData = zonePropertyDataFrom(zonePropertiesDataFile,
        areaTypeRepository);
    AttractivitiesData attractivityData = attractivityDataFrom(attractivityDataFile);
    ParkingFacilityDataRepository parkingFacilitiesData = parkingFacilitiesDataFrom(parkingFacilitiesDataFile);
		CarSharingDataRepository carSharingStationsData = carSharingStationsDataFrom(visumNetwork,
				roadNetwork, carSharingStationsDataFile);
    return new ZonesReaderCsvBased(visumNetwork, roadNetwork, zonePropertiesData, attractivityData, parkingFacilitiesData,
    		carSharingStationsData, charging, defaultPower, mapper);
  }

  private static ParkingFacilityDataRepository parkingFacilitiesDataFrom(
  		File parkingFacilitiesDataFile) {
		if (parkingFacilitiesDataFile.exists()) {
			CsvFile dataFile = CsvFile.createFrom(parkingFacilitiesDataFile.getAbsolutePath());
			StructuralData structuralData = new StructuralData(dataFile);
			return (zone, id) -> structuralData.valueOrDefault(id.getExternalId(), "numberofparkingplaces");
		}
		System.out
				.println(
						"parking facility data file is not available - will try to get parkingfacilities from visum zone information!");
		return (zone, id) -> zone.parkingPlaces;
  }
  
  private static AttractivitiesData attractivityDataFrom(
      File structuralDataFile) {
    StructuralData dataFile = new StructuralData(CsvFile.createFrom(structuralDataFile.getAbsolutePath()));
    return new AttractivitiesData(dataFile);
  }

  private static ZonePropertiesData zonePropertyDataFrom(
      File structuralDataFile, AreaTypeRepository areaTypeRepository) {
    StructuralData dataFile = new StructuralData(CsvFile.createFrom(structuralDataFile.getAbsolutePath()));
    return new ZonePropertiesData(dataFile, areaTypeRepository);
  }

  private static CarSharingDataRepository carSharingStationsDataFrom(
  		VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, File carSharingStationsDataFile) {
  	IdSequence carIds = new IdSequence();
		if (carSharingStationsDataFile.exists()) {
			CsvFile stationData = CsvFile.createFrom(carSharingStationsDataFile.getAbsolutePath());
			return new FileBasedCarSharingDataRepository(roadNetwork, stationData, carIds);
		}
		System.out
				.println(
						"carsharingstation data file is not available - will try to get carsharingstation data from visum zone information!");
		return new VisumBasedCarSharingDataRepository(visumNetwork, roadNetwork, carIds);
  }
}
