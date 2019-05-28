package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumZoneReader extends VisumBaseReader {

  static final double alwaysAllowed = 1.0;
  
  public VisumZoneReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, VisumZone> readZones(Stream<Row> content) {
    return content
        .map(this::zoneWithChargingStations)
        .collect(toMap(z -> z.id, Function.identity()));
  }

  private VisumZone zoneWithChargingStations(Row row) {
    int id = row.valueAsInteger(number());
    String name = row.get(name());
    int mainZoneId = row.valueAsInteger(attribute(StandardAttributes.mainZoneNumber));
    int type = row.valueAsInteger(typeNumber());
    int parkingPlaces = parkingPlaces(row);
    float coord_x = row.valueAsFloat(xCoord());
    float coord_y = row.valueAsFloat(yCoord());
    int areaId = row.valueAsInteger(areaId());
    int chargingFacilities = chargingFacilities(row);
    int freeFloatingCarSharingArea = car2GoGebiet(row);
    String freeFloatingCarSharingCompany = 1 == freeFloatingCarSharingArea ? "Car2Go" : "";
    int freeFloatingCarSharingCars = floatingCarNumber(row);
    double privateChargingProbability = privateChargingProbability(row);
    Map<String, Float> carsharingcarDensities = carSharingDensities(row);
    float innerZonePublicTransport = innerZonePublicTransportTime(row);
    return new VisumZone(id, name, mainZoneId, type, parkingPlaces, coord_x, coord_y, areaId,
        chargingFacilities, freeFloatingCarSharingCompany, freeFloatingCarSharingArea,
        freeFloatingCarSharingCars, privateChargingProbability, carsharingcarDensities,
        innerZonePublicTransport);
  }

  private int parkingPlaces(Row row) {
    String attribute = attribute(StandardAttributes.parkingPlaces);
    if (row.containsAttribute(attribute)) {
      String value = row.get(attribute);
      if (value.isEmpty()) {
        return 0;
      }
      return Double.valueOf(value).intValue();
    }
    return 0;
  }

  private float innerZonePublicTransportTime(Row row) {
    String diagPt = attribute(StandardAttributes.innerZonePublicTransportTravelTime);
    if (row.containsAttribute(diagPt)) {
      return row.valueAsFloat(diagPt);
    }
    System.out.println(
        "No travel time for public transport inside a single zone is given. Using 0 as travel time.");
    return 0.0f;
  }

  private Map<String, Float> carSharingDensities(Row row) {
    Map<String, Float> carsharingcarDensities = new HashMap<>();
    String fzFlSm = attribute(StandardAttributes.carSharingDensityStadtmobil);
    String fzFlFl = attribute(StandardAttributes.carSharingDensityFlinkster);
    String fzFlC2g = attribute(StandardAttributes.carSharingDensityCar2Go);
    if (row.containsAttribute(fzFlSm) && row.containsAttribute(fzFlFl)
        && row.containsAttribute(fzFlC2g)) {
      carsharingcarDensities.put("Stadtmobil", row.valueAsFloat(fzFlSm));
      carsharingcarDensities.put("Flinkster", row.valueAsFloat(fzFlFl));
      carsharingcarDensities.put("Car2Go", row.valueAsFloat(fzFlC2g));
    }
    return carsharingcarDensities;
  }

  private int floatingCarNumber(Row row) {
    String car2GoStartState = attribute(StandardAttributes.car2GoStartState);
    if (row.containsAttribute(car2GoStartState)) {
      String floatingCarNumber = row.get(car2GoStartState);
      return floatingCarNumber.isEmpty() ? 0 : Integer.parseInt(floatingCarNumber);
    }
    return 0;
  }

  private int car2GoGebiet(Row row) {
    String car2GoTerritory = attribute(StandardAttributes.car2GoTerritory);
    if (row.containsAttribute(car2GoTerritory)) {
      String floatingCarArea = row.get(car2GoTerritory);
      return floatingCarArea.isEmpty() ? 0 : Integer.parseInt(floatingCarArea);
    }
    return 0;
  }

  private int chargingFacilities(Row row) {
    String chargingStations = attribute(StandardAttributes.chargingStations);
    if (row.containsAttribute(chargingStations)) {
      return row.valueAsInteger(chargingStations);
    }
    return 0;
  }

  private double privateChargingProbability(Row row) {
    String privateChargingProbability = attribute(StandardAttributes.privateChargingProbability);
    if (row.containsAttribute(privateChargingProbability)) {
      String anteilSte = row.get(privateChargingProbability);
      return anteilSte.isEmpty() ? alwaysAllowed : normalizeProbability(anteilSte);
    }
    return alwaysAllowed;
  }

  private static double normalizeProbability(String anteilSte) {
    double value = Double.parseDouble(anteilSte);
    return value / 100.0d;
  }

}
