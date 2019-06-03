package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumTerritoryReader extends VisumBaseReader {

  private final SortedMap<Integer, VisumSurface> polygons;

  public VisumTerritoryReader(NetfileLanguage language, SortedMap<Integer, VisumSurface> polygons) {
    super(language);
    this.polygons = Collections.unmodifiableSortedMap(polygons);
  }

  public Map<Integer, VisumTerritory> readTerritories(Stream<Row> content) {
    return content.map(this::createTerritory).collect(toMap(t -> t.id, Function.identity()));
  }

  VisumTerritory createTerritory(Row row) {
    int id = row.valueAsInteger(number());
    String code = code(row);
    String name = nameOf(row);
    int areaId = row.valueAsInteger(areaId());
    VisumSurface area = polygons.get(areaId);

    VisumTerritory t = new VisumTerritory(id, code, name, areaId, area);
    return t;
  }

  private String nameOf(Row row) {
    if (row.containsAttribute(name())) {
      return row.get(name());
    }
    return row.get(attribute(StandardAttributes.item));
  }

  private String code(Row row) {
    String codeLc = attribute(StandardAttributes.codeLc);
    if (row.containsAttribute(codeLc)) {
      return row.get(codeLc);
    }
    return "";
  }

}
