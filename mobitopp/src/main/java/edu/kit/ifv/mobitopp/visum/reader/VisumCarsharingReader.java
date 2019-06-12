package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.Row;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumCarSharingStation;

public class VisumCarsharingReader extends VisumBaseReader {

  public VisumCarsharingReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, VisumCarSharingStation> readCarsharingStations(Stream<Row> content) {
    return content.map(this::createStation).collect(toMap(s -> s.id, Function.identity()));
  }

  private VisumCarSharingStation createStation(Row row) {
    int id = readStadtMobilId(row);
    String name = nameOf(row);
    float xCoord = xCoordOf(row);
    float yCoord = yCoordOf(row);
    int numberOfCars = Double.valueOf(row.get(numberOfVehicles())).intValue();
    String address = row.get(town()) + " / " + row.get(streetIso8859());
    String type = typeOf(row);
    return new VisumCarSharingStation(id, name, xCoord, yCoord, numberOfCars, address, type);
  }

  private int readStadtMobilId(Row row) {
    String attribute = attribute(StandardAttributes.objectId);
    if (row.containsAttribute(attribute)) {
      return Double.valueOf(row.get(attribute)).intValue();
    }
    return Double.valueOf(row.get(id())).intValue();
  }

  private String streetIso8859() {
    return attribute(StandardAttributes.streetIso8859);
  }

  private String town() {
    return attribute(StandardAttributes.town);
  }

  private String numberOfVehicles() {
    return attribute(StandardAttributes.numberOfVehicles);
  }

}
