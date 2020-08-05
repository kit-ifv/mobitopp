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
import edu.kit.ifv.mobitopp.data.ZoneProperties;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.local.ChargingType;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.bikesharing.BikeSharingDataForZone;
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
  private final ZonePropertiesData zonePropertiesData;
  private final AttractivitiesData attractivities;

  private final ZoneLocationSelector locationSelector;
  private final IdToOidMapper idToOid;
  private final ParkingFacilityDataRepository parkingFacilitiesDataRepository;
  private final BikeSharingDataRepository bikeSharingDataRepository;
	private final CarSharingDataRepository carSharingDataRepository;

	ZonesReaderCsvBased(
			final VisumNetwork visumNetwork, 
			final SimpleRoadNetwork roadNetwork, 
			final ZonePropertiesData zoneproperties,
			final AttractivitiesData attractivities,
			final ParkingFacilityDataRepository parkingFacilitiesDataRepository,
			final BikeSharingDataRepository bikeSharingDataRepository, 
			final CarSharingDataRepository carSharingDataRepository, 
			final ChargingType charging,
			final DefaultPower defaultPower, 
			final IdToOidMapper mapper) {
		super();
    this.visumNetwork = visumNetwork;
    this.zonePropertiesData = zoneproperties;
    this.attractivities = attractivities;
    this.parkingFacilitiesDataRepository = parkingFacilitiesDataRepository;
    this.bikeSharingDataRepository = bikeSharingDataRepository;
    this.carSharingDataRepository = carSharingDataRepository;
    this.idToOid = mapper;
    ChargingDataFactory factory = charging.factory(defaultPower);
    locationSelector = new ZoneLocationSelector(roadNetwork);
    chargingDataBuilder = new ChargingDataBuilder(visumNetwork, locationSelector, factory,
        defaultPower);
  }

	@Override
	public List<Zone> getZones() {
		zonePropertiesData.data().resetRow();
		ArrayList<VisumZone> visumZones = new ArrayList<>(visumNetwork.zones.values());
		Collections.sort(visumZones, Comparator.comparing(zone -> zone.id));
		List<Zone> zones = new ArrayList<>();
		while (zonePropertiesData.data().hasNext()) {
			VisumZone visumZone = visumNetwork.zones.get(zonePropertiesData.data().currentRegion());
			zones.add(zoneFrom(visumZone));
			System.out
					.println(
							String.format("Processed zone %1d of %2d zones", visumZone.id, visumZones.size()));
			zonePropertiesData.data().next();
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
    boolean isDestination = isDestination(visumId);
    double relief = relief(visumId);
    Attractivities attractivities = attractivities(visumId);
		ChargingDataForZone chargingData = chargingData(visumZone, polygon);
		ZoneProperties zoneProperties = ZoneProperties
				.builder()
				.name(name)
				.areaType(areaType)
				.regionType(regionType)
				.classification(classification)
				.parkingPlaces(parkingPlaces)
				.isDestination(isDestination)
				.centroidLocation(centroid)
				.relief(relief)
				.build();
		Zone zone = new Zone(zoneId, zoneProperties, attractivities, chargingData);
		BikeSharingDataForZone bikeSharingData = getBikeSharingData(zone);
		zone.setBikeSharing(bikeSharingData);
    CarSharingDataForZone carSharingData = getCarSharingData(visumZone, polygon, zone);
    zone.setCarSharing(carSharingData);
    zone.setMaas(MaasDataForZone.everywhereAvailable());
    return zone;
  }

	private BikeSharingDataForZone getBikeSharingData(Zone zone) {
		return bikeSharingDataRepository.getData(zone.getId());
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
    return zonePropertiesData.currentClassification(zoneId);
  }

  private RegionType regionType(String zoneId) {
    return zonePropertiesData.currentRegionType(zoneId);
  }
  
  private AreaType currentZoneAreaType(String zoneId) {
    return zonePropertiesData.currentZoneAreaType(zoneId);
  }

	private boolean isDestination(String zoneId) {
		return zonePropertiesData.isDestination(zoneId);
	}

	private double relief(String zoneId) {
		return zonePropertiesData.relief(zoneId);
	}

	public static ZonesReaderCsvBased from(
			final VisumNetwork visumNetwork, 
			final SimpleRoadNetwork roadNetwork, 
			final ChargingType charging,
			final DefaultPower defaultPower, 
			final File zonePropertiesDataFile, 
			final File attractivityDataFile,
			final File parkingFacilitiesDataFile, 
			final File carSharingPropertiesFile, 
			final File stationsDataFile,
			final File freeFloatingDataFile, 
			final File bikeSharingPropertiesFile,
			final AreaTypeRepository areaTypeRepository, 
			final IdToOidMapper mapper) {
		ZonePropertiesData zonePropertiesData = zonePropertyDataFrom(zonePropertiesDataFile,
        areaTypeRepository);
    AttractivitiesData attractivityData = attractivityDataFrom(attractivityDataFile);
    ParkingFacilityDataRepository parkingFacilitiesData = parkingFacilitiesDataFrom(parkingFacilitiesDataFile);
		BikeSharingDataRepository bikeSharingData = bikeSharingDataFrom(
				bikeSharingPropertiesFile, mapper);
		CarSharingDataRepository carSharingStationsData = carSharingStationsDataFrom(visumNetwork,
				roadNetwork, carSharingPropertiesFile, stationsDataFile, freeFloatingDataFile);
    return new ZonesReaderCsvBased(visumNetwork, roadNetwork, zonePropertiesData, attractivityData, parkingFacilitiesData,
    		bikeSharingData, carSharingStationsData, charging, defaultPower, mapper);
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

	private static BikeSharingDataRepository bikeSharingDataFrom(File bikeSharingPropertiesFile, IdToOidMapper mapper) {
		StructuralData properties = new StructuralData(CsvFile.createFrom(bikeSharingPropertiesFile));
		return new BikeSharingPropertiesData(properties, mapper);
	}

	private static CarSharingDataRepository carSharingStationsDataFrom(
			VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, File carSharingPropertiesFile,
			File stationsDataFile, File freeFloatingDataFile) {
		IdSequence carIds = new IdSequence();
		if (stationsDataFile.exists()) {
			CsvFile properties = CsvFile.createFrom(carSharingPropertiesFile.getAbsolutePath());
			CsvFile stationData = CsvFile.createFrom(stationsDataFile.getAbsolutePath());
			CsvFile freeFloatingData = CsvFile.createFrom(freeFloatingDataFile.getAbsolutePath());
			return new FileBasedCarSharingDataRepository(roadNetwork, properties, stationData,
					freeFloatingData, carIds);
		}
		System.out
				.println(
						"carsharingstation data file is not available - will try to get carsharingstation data from visum zone information!");
		return new VisumBasedCarSharingDataRepository(visumNetwork, roadNetwork, carIds);
  }
}
