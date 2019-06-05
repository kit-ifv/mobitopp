package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombinationUnit;
import edu.kit.ifv.mobitopp.visum.VisumVehicleUnit;
import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumVehicleUnitsToCombinationsReader extends VisumBaseReader {

  private final Map<Integer, VisumVehicleUnit> vehicleUnits;

  public VisumVehicleUnitsToCombinationsReader(
      NetfileLanguage language, Map<Integer, VisumVehicleUnit> vehicleUnits) {
    super(language);
    this.vehicleUnits = vehicleUnits;
  }

  public Map<Integer, List<VisumVehicleCombinationUnit>> readMapping(Stream<Row> content) {
    return content.map(this::create).collect(groupingBy(m -> m.combinationId));
  }

  private VisumVehicleCombinationUnit create(Row row) {
    int combinationId = vehicleCombinationOf(row);
    VisumVehicleUnit unit = vehicleUnit(row);
    int quantity = numberOfVehicleUnits(row);
    return new VisumVehicleCombinationUnit(combinationId, unit, quantity);
  }

  private int numberOfVehicleUnits(Row row) {
    return row.valueAsInteger(attribute(StandardAttributes.numberOfVehicleUnits));
  }

  private VisumVehicleUnit vehicleUnit(Row row) {
    int unitId = row.valueAsInteger(attribute(StandardAttributes.vehicleUnitNumber));
    return vehicleUnits.get(unitId);
  }
}
