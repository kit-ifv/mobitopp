package edu.kit.ifv.mobitopp.visum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class ContentDescription {

  private final long startOfContent;
  private final long endOfContent;
  private final List<String> attributes;
  private final String attributeSeparator;

  public ContentDescription(long startOfContent, long endOfContent, List<String> attributes, String attributeSeparator) {
    super();
    this.startOfContent = startOfContent;
    this.endOfContent = endOfContent;
    this.attributes = attributes;
    this.attributeSeparator = attributeSeparator;
  }

  public Stream<Row> convert(Stream<String> lines) {
    return lines
        .skip(startOfContent)
        .limit(endOfContent - startOfContent)
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
  public String toString() {
    return "ContentDescription [startOfContent=" + startOfContent + ", endOfContent=" + endOfContent
        + ", attributes=" + attributes + ", attributeSeparator=" + attributeSeparator + "]";
  }

}
