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
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.local.ChargingType;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ZonesReaderCsvBased implements ZonesReader {

  private final VisumNetwork visumNetwork;
  private final SimpleRoadNetwork roadNetwork;
  private final ChargingDataBuilder chargingDataBuilder;
  private final AttractivitiesData attractivities;
  private final ZoneLocationSelector locationSelector;

  ZonesReaderCsvBased(
      VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, AttractivitiesData attractivities,
      ChargingType charging, DefaultPower defaultPower) {
    super();
    this.visumNetwork = visumNetwork;
    this.roadNetwork = roadNetwork;
    this.attractivities = attractivities;
    ChargingDataFactory factory = charging.factory(defaultPower);
    locationSelector = new ZoneLocationSelector(roadNetwork);
    chargingDataBuilder = new ChargingDataBuilder(visumNetwork, locationSelector, factory,
        defaultPower);
  }

  @Override
  public List<Zone> getZones() {
    Zone.resetIdSequence();
    attractivities.resetIndex();
    ArrayList<VisumZone> visumZones = new ArrayList<>(visumNetwork.zones.values());
    Collections.sort(visumZones, Comparator.comparing(zone -> zone.id));
    List<Zone> zones = new ArrayList<>();
    while (attractivities.hasNext()) {
      VisumZone visumZone = visumNetwork.zones.get(attractivities.currentZone());
      zones.add(zoneFrom(visumZone));
      System.out
          .println(
              String.format("Processed zone %1d of %2d zones", visumZone.id, visumZones.size()));
      attractivities.next();
    }
    return zones;
  }

  private Zone zoneFrom(VisumZone visumZone) {
    String visumId = String.valueOf(visumZone.id);
    String id = String.format("Z%1d", visumZone.id);
    String name = visumZone.name;
    AreaType areaType = currentZoneAreaType(visumId);
    RegionType regionType = regionType(visumId);
    ZoneClassificationType classification = currentClassification(visumId);
    ZonePolygon polygon = currentZonePolygon(visumZone);
    Location centroid = polygon.centroidLocation();
    Attractivities attractivities = attractivities(visumId);
    ChargingDataForZone chargingData = chargingData(visumZone, polygon);
    Zone zone = new Zone(id, name, areaType, regionType, classification, centroid, attractivities,
        chargingData);
    CarSharingDataForZone carSharingData = carSharing(visumZone, polygon, zone);
    zone.setCarSharing(carSharingData);
    zone.setMaas(MaasDataForZone.everywhereAvailable());
    return zone;
  }


  private CarSharingDataForZone carSharing(VisumZone visumZone, ZonePolygon polygon, Zone zone) {
    return carSharingBuilder().carsharingIn(visumZone, polygon, zone);
  }

  CarSharingBuilder carSharingBuilder() {
    return new CarSharingBuilder(visumNetwork, roadNetwork);
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
    return attractivities.currentClassification(zoneId);
  }

  private RegionType regionType(String zoneId) {
    return attractivities.currentRegionType(zoneId);
  }
  
  private AreaType currentZoneAreaType(String zoneId) {
    return attractivities.currentZoneAreaType(zoneId);
  }

  public static ZonesReaderCsvBased from(
      VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, ChargingType charging,
      DefaultPower defaultPower, File attractivityDataFile, AreaTypeRepository areaTypeRepository) {
    AttractivitiesData attractivityData = structuralDataFrom(attractivityDataFile,
        areaTypeRepository);
    return new ZonesReaderCsvBased(visumNetwork, roadNetwork, attractivityData, charging,
        defaultPower);
  }

  private static AttractivitiesData structuralDataFrom(
      File structuralDataFile, AreaTypeRepository areaTypeRepository) {
    StructuralData dataFile = new StructuralData(new CsvFile(structuralDataFile.getAbsolutePath()));
    return new AttractivitiesData(dataFile, areaTypeRepository);
  }

}
