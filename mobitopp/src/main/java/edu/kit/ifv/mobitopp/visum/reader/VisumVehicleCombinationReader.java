package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.Row;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombination;
import edu.kit.ifv.mobitopp.visum.VisumVehicleCombinationUnit;
import edu.kit.ifv.mobitopp.visum.VisumVehicleUnit;

public class VisumVehicleCombinationReader extends VisumBaseReader {

  private final Map<Integer, List<VisumVehicleCombinationUnit>> units2combinations;

  public VisumVehicleCombinationReader(
      NetfileLanguage language,
      Map<Integer, List<VisumVehicleCombinationUnit>> units2combinations) {
    super(language);
    this.units2combinations = units2combinations;
  }

  public Map<Integer, VisumVehicleCombination> readCombinations(Stream<Row> content) {
    return content.map(this::createCombination).collect(toMap(c -> c.id, Function.identity()));
  }

  private VisumVehicleCombination createCombination(Row row) {
    int id = numberOf(row);
    String code = codeOf(row);
    String name = nameOf(row);
    Map<VisumVehicleUnit, Integer> units = units(id);
    return new VisumVehicleCombination(id, code, name, units);
  }

  private Map<VisumVehicleUnit, Integer> units(Integer id) {
    return units2combinations.get(id).stream().collect(toMap(u -> u.unit, u -> u.quantity));
  }

}
