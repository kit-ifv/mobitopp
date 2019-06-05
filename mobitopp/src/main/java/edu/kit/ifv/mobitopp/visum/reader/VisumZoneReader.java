package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumZone;
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
    int id = numberOf(row);
    String name = nameOf(row);
    int mainZoneId = row.valueAsInteger(attribute(StandardAttributes.mainZoneNumber));
    int type = typeNumberOf(row);
    int parkingPlaces = parkingPlacesOf(row);
    float coord_x = xCoordOf(row);
    float coord_y = yCoordOf(row);
    int areaId = areaIdOf(row);
    int chargingFacilities = chargingFacilitiesOf(row);
    int freeFloatingCarSharingArea = car2GoGebietOf(row);
    String freeFloatingCarSharingCompany = 1 == freeFloatingCarSharingArea ? "Car2Go" : "";
    int freeFloatingCarSharingCars = floatingCarNumberOf(row);
    double privateChargingProbability = privateChargingProbabilityOf(row);
    Map<String, Float> carsharingcarDensities = carSharingDensitiesOf(row);
    float innerZonePublicTransport = innerZonePublicTransportTimeOf(row);
    return new VisumZone(id, name, mainZoneId, type, parkingPlaces, coord_x, coord_y, areaId,
        chargingFacilities, freeFloatingCarSharingCompany, freeFloatingCarSharingArea,
        freeFloatingCarSharingCars, privateChargingProbability, carsharingcarDensities,
        innerZonePublicTransport);
  }

  private int parkingPlacesOf(Row row) {
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

  private float innerZonePublicTransportTimeOf(Row row) {
    String diagPt = attribute(StandardAttributes.innerZonePublicTransportTravelTime);
    if (row.containsAttribute(diagPt)) {
      return row.valueAsFloat(diagPt);
    }
    System.out.println(
        "No travel time for public transport inside a single zone is given. Using 0 as travel time.");
    return 0.0f;
  }

  private Map<String, Float> carSharingDensitiesOf(Row row) {
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

  private int floatingCarNumberOf(Row row) {
    String car2GoStartState = attribute(StandardAttributes.car2GoStartState);
    if (row.containsAttribute(car2GoStartState)) {
      String floatingCarNumber = row.get(car2GoStartState);
      return floatingCarNumber.isEmpty() ? 0 : Integer.parseInt(floatingCarNumber);
    }
    return 0;
  }

  private int car2GoGebietOf(Row row) {
    String car2GoTerritory = attribute(StandardAttributes.car2GoTerritory);
    if (row.containsAttribute(car2GoTerritory)) {
      String floatingCarArea = row.get(car2GoTerritory);
      return floatingCarArea.isEmpty() ? 0 : Integer.parseInt(floatingCarArea);
    }
    return 0;
  }

  private int chargingFacilitiesOf(Row row) {
    String chargingStations = attribute(StandardAttributes.chargingStations);
    if (row.containsAttribute(chargingStations)) {
      return row.valueAsInteger(chargingStations);
    }
    return 0;
  }

  private double privateChargingProbabilityOf(Row row) {
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
