package edu.kit.ifv.mobitopp.visum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class TableDescription {

  private final long startOfTable;
  private final long endOfTable;
  private final List<String> attributes;
  private final String attributeSeparator;

  public TableDescription(long startOfTable, long endOfTable, List<String> attributes, String attributeSeparator) {
    super();
    this.startOfTable = startOfTable;
    this.endOfTable = endOfTable;
    this.attributes = attributes;
    this.attributeSeparator = attributeSeparator;
  }

  public Stream<Row> convert(Stream<String> lines) {
    return lines
        .skip(startOfTable)
        .limit(endOfTable - startOfTable)
        .map(line -> parseLine(line, attributes.size()))
        .map(line -> Row.createRow(line, attributes));
  }

  private List<String> parseLine(String line, int numFields) {
    String[] fields = line.split(attributeSeparator);
    List<String> values = new ArrayList<>(numFields);
    values.addAll(Arrays.asList(fields));
    for (int i = values.size(); i < numFields; i++) {
      values.add("");
    }
    return values;
  }

  @Override
  public int hashCode() {
    return Objects.hash(attributeSeparator, attributes, endOfTable, startOfTable);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TableDescription other = (TableDescription) obj;
    return Objects.equals(attributeSeparator, other.attributeSeparator)
        && Objects.equals(attributes, other.attributes) && endOfTable == other.endOfTable
        && startOfTable == other.startOfTable;
  }

  @Override
  public String toString() {
    return "TableDescription [startOfTable=" + startOfTable + ", endOfTable=" + endOfTable
        + ", attributes=" + attributes + ", attributeSeparator=" + attributeSeparator + "]";
  }

}
