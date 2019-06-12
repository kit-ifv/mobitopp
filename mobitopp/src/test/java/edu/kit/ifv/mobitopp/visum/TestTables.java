package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;

import java.util.List;

import edu.kit.ifv.mobitopp.result.CsvBuilder;

public class TestTables {

  public final String someTable;
  public final List<String> someAttributes;
  public final List<String> someValues;
  public final String otherTable;
  public final List<String> otherAttributes;
  public final List<String> otherValues;
  private final CsvBuilder content;

  public TestTables() {
    super();
    someTable = "some-table";
    someAttributes = asList("ID", "some-column");
    someValues = asList("1", "2");
    otherTable = "other-table";
    otherAttributes = asList("ID", "other-column");
    otherValues = asList("3", "4");
    content = new CsvBuilder();
  }

  public void addOtherTable() {
    addTable(otherTable, otherValues, otherAttributes);
  }

  public void addSomeTable() {
    addTable(someTable, someValues, someAttributes);
  }

  public void addTable(String tableName, List<String> values, List<String> attributes) {
    content.append("$" + tableName + ":" + attributes.get(0));
    content.newLine(attributes.get(1));
    content.append(values.get(0));
    content.newLine(values.get(1));
    content.newLine("");
  }

  @Override
  public String toString() {
    return content.toString();
  }

  public void addCommentLine() {
    content.newLine("* some comment");
  }

  public void addEmptyLine() {
    content.newLine("");
  }

}
