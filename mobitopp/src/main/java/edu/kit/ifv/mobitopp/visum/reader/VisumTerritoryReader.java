package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumTerritory;

public class VisumTerritoryReader extends VisumBaseReader {

  private final SortedMap<Integer, VisumSurface> polygons;

  public VisumTerritoryReader(NetfileLanguage language, SortedMap<Integer, VisumSurface> polygons) {
    super(language);
    this.polygons = Collections.unmodifiableSortedMap(polygons);
  }

  public Map<Integer, VisumTerritory> readTerritories(Stream<Row> content) {
    return content.map(this::createTerritory).collect(toMap(t -> t.id, Function.identity()));
  }

  private VisumTerritory createTerritory(Row row) {
    int id = numberOf(row);
    String code = code(row);
    String name = nameOf(row);
    int areaId = areaIdOf(row);
    VisumSurface area = polygons.get(areaId);
    return new VisumTerritory(id, code, name, areaId, area);
  }

  private String code(Row row) {
    String codeLc = attribute(StandardAttributes.codeLc);
    if (row.containsAttribute(codeLc)) {
      return row.get(codeLc);
    }
    return "";
  }

}
