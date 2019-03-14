package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.Value;

public class ColumnMapping<T> {

  private static final int noOffset = 0;
  private final LinkedHashMap<String, Function<T, Object>> columns;
  private final int offset;
  private ArrayList<String> header;

  public ColumnMapping(int offset) {
    super();
    this.offset = offset;
    this.columns = new LinkedHashMap<>();
  }

  public ColumnMapping() {
    this(noOffset);
  }

  public void add(String columnName, Function<T, Object> resolver) {
    storeColumn(columnName, resolver);
    resetHeader();
  }

  private void storeColumn(String columnName, Function<T, Object> resolver) {
    columns.put(columnName, resolver);
  }

  private void resetHeader() {
    header = null;
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
    if (null == header) {
      header = new ArrayList<>(columns.keySet());
    }
    return header;
  }

  public Value get(String field, List<String> data) {
    int index = offset + header().indexOf(field);
    return new Value(data.get(index));
  }

}
