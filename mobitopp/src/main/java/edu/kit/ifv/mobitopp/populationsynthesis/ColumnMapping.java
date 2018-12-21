package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Value;

public class ColumnMapping<T> {

  private final LinkedHashMap<String, Function<T, Object>> columns;

  public ColumnMapping() {
    super();
    this.columns = new LinkedHashMap<>();
  }

  public void add(String columnName, Function<T, Object> resolver) {
    columns.put(columnName, resolver);
  }

  public List<String> prepare(T element) {
    return columns
        .values()
        .stream()
        .map(f -> f.apply(element))
        .map(Object::toString)
        .collect(toList());
  }

  public List<String> header() {
    return new ArrayList<>(columns.keySet());
  }

  public Value get(String field, List<String> data) {
    int index = new ArrayList<>(columns.keySet()).indexOf(field);
    return new Value(data.get(index));
  }

}
