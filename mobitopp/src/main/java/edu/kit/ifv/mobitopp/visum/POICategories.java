package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class POICategories {

  private final Map<String, Integer> codeToNumber;

  private POICategories(Map<String, Integer> codeToNumber) {
    super();
    this.codeToNumber = codeToNumber;
  }

  public static POICategories from(VisumTable table) {
    Objects.requireNonNull(table);
    assert table.containsAttribute("NR");
    assert table.containsAttribute("CODE");
    return from(table.rows());
  }

  public static POICategories from(Collection<Row> rows) {
    return from(rows.stream());
  }

  public static POICategories from(Stream<Row> streamedRows) {
    Map<String, Integer> codeToNumber = streamedRows
        .collect(toMap(row -> row.get("CODE"), row -> row.valueAsInteger("NR")));
    return new POICategories(codeToNumber);
  }

  public boolean containsCode(String code) {
    return codeToNumber.containsKey(code);
  }

  public int numberByCode(String code) {
    if (codeToNumber.containsKey(code)) {
      return codeToNumber.get(code);
    }
    throw new IllegalArgumentException(String
        .format("Cannot resolve code %s to number. Available codes are: %s", code,
            codeToNumber.keySet()));
  }

  public boolean containsNumber(int number) {
    return codeToNumber.containsValue(number);
  }

}
