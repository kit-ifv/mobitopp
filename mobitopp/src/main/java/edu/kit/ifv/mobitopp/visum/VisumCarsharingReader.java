package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumCarsharingReader extends VisumBaseReader {

  public VisumCarsharingReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, VisumCarSharingStation> readCarsharingStations(Stream<Row> content) {
    return content.map(this::createStation).collect(toMap(s -> s.id, Function.identity()));
  }

  VisumCarSharingStation createStation(Row row) {
    int id = readStadtMobilId(row);
    String name = row.get(name());
    float xCoord = row.valueAsFloat(xCoord());
    float yCoord = row.valueAsFloat(yCoord());
    int numberOfCars = Double.valueOf(row.get(numberOfVehicles())).intValue();
    String address = row.get(town()) + " / " + row.get(streetIso8859());
    String type = row.get(type());
    VisumCarSharingStation tmp = new VisumCarSharingStation(
                                  id,
                                  name,
                                  xCoord,
                                  yCoord,
                                  numberOfCars,
                                  address,
                                  type
                        );
    return tmp;
  }

  private int readStadtMobilId(Row row) {
    String attribute = attribute(StandardAttributes.objectId);
    if (row.containsAttribute(attribute)) {
      return Double.valueOf(row.get(attribute)).intValue();
    }
    return Double.valueOf(row.get(id())).intValue();
  }

}
