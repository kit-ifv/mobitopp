package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumChargingFacilityReader extends VisumBaseReader {

  public VisumChargingFacilityReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, VisumChargingFacility> readStations(Stream<Row> content) {
    return content.map(this::createChargingFacility).collect(toMap(c -> c.id, Function.identity()));
  }

  public VisumChargingFacility createChargingFacility(Row row) {
    int id = row.valueAsInteger(attribute(StandardAttributes.lsId));
    Double latitude = row.valueAsDouble(attribute(StandardAttributes.latitude));
    Double longitude = row.valueAsDouble(attribute(StandardAttributes.longitude));
    float coord_x = row.valueAsFloat(xCoord());
    float coord_y = row.valueAsFloat(yCoord());
    String type = row.get(attribute(StandardAttributes.chargingType));
    String vehicleType = row.get(attribute(StandardAttributes.vehicleType));
    String publicType = row.get(attribute(StandardAttributes.publicType));
    String place = row.get(attribute(StandardAttributes.place));
    String postalCode = row.get(attribute(StandardAttributes.plz));
    String street = row.get(attribute(StandardAttributes.street));
    String address = place + ", " + postalCode + ", " + street;
    VisumChargingFacility tmp = new VisumChargingFacility(
                                  id,
                                  coord_x,
                                  coord_y,
                                  type,
                                  vehicleType,
                                  publicType,
                                  latitude,
                                  longitude,
                                  address
                        );
    return tmp;
  }

}
