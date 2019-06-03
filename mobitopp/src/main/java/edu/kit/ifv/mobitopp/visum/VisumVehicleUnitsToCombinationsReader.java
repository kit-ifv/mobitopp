package edu.kit.ifv.mobitopp.visum;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumVehicleUnitsToCombinationsReader extends VisumBaseReader {

  public VisumVehicleUnitsToCombinationsReader(NetfileLanguage language) {
    super(language);
  }

  public Map<Integer, Map<Integer, Integer>> readMapping(VisumTable table) {
    Map<Integer, Map<Integer, Integer>> units2combinations = new HashMap<>();
    for (int i=0; i<table.numberOfRows(); i++) {
      Row row = table.getRow(i);
      Integer combinationId = vehicleCombination(row);
      Integer unitId = row.valueAsInteger(attribute(StandardAttributes.vehicleUnitNumber));
      Integer quantity = row.valueAsInteger(attribute(StandardAttributes.numberOfVehicleUnits));

      if (!units2combinations.containsKey(combinationId)) {
        units2combinations.put(combinationId, new HashMap<Integer,Integer>());
      }

      units2combinations.get(combinationId).put(unitId,quantity);
    }
    return units2combinations;
  }

  private Integer vehicleCombination(Row row) {
    String combination = row.get(vehicleCombinationNumber());
    if (combination.isEmpty()) {
      return 0;
    }
    return Integer.valueOf(combination);
  }
}
