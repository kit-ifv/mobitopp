package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class POICategories {

  private final Map<String, Integer> codeToNumber;

  private POICategories(Map<String, Integer> codeToNumber) {
    super();
    this.codeToNumber = codeToNumber;
  }

  public static POICategories from(VisumTable table, NetfileLanguage language) {
    Objects.requireNonNull(table);
    assert table.containsAttribute("NR");
    assert table.containsAttribute("CODE");
    return from(table.rows(), language);
  }

  public static POICategories from(Collection<Row> rows, NetfileLanguage language) {
    return from(rows.stream(), language);
  }

  public static POICategories from(Stream<Row> streamedRows, NetfileLanguage language) {
    String code = language.resolve(StandardAttributes.code);
    String number = language.resolve(StandardAttributes.number);
    Map<String, Integer> codeToNumber = streamedRows
        .collect(toMap(row -> row.get(code), row -> row.valueAsInteger(number)));
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
