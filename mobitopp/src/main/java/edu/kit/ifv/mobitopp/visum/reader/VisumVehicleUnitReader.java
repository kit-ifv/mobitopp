package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.Row;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
import edu.kit.ifv.mobitopp.visum.VisumVehicleUnit;

public class VisumVehicleUnitReader extends VisumBaseReader {

  private final VisumTransportSystems allSystems;

  public VisumVehicleUnitReader(NetfileLanguage language, VisumTransportSystems allSystems) {
    super(language);
    this.allSystems = allSystems;
  }

  public Map<Integer, VisumVehicleUnit> readVehicleUnits(Stream<Row> content) {
    return content.map(this::createUnit).collect(toMap(u -> u.id, Function.identity()));
  }

  private VisumVehicleUnit createUnit(Row row) {
    int id = numberOf(row);
    VisumTransportSystemSet systemSet = transportSystemsOf(row, allSystems);
    String code = codeOf(row);
    String name = nameOf(row);
    Integer capacity = row.valueAsInteger(attribute(StandardAttributes.vehicleCapacity));
    int seats = row.valueAsInteger(attribute(StandardAttributes.seats));
    return new VisumVehicleUnit(id, code, name, systemSet, capacity, seats);
  }

}
