package edu.kit.ifv.mobitopp.visum.routes;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.kit.ifv.mobitopp.result.CsvBuilder;

public class BaseStreamReaderTest {

  private CsvBuilder content;
  private String someTable;
  private List<String> someAttributes;
  private List<String> someValues;
  private String otherTable;
  private List<String> otherAttributes;
  private List<String> otherValues;

  @BeforeEach
  public void initialise() {
    someTable = "some-table";
    someAttributes = asList("ID", "some-column");
    someValues = asList("1", "2");
    otherTable = "other-table";
    otherAttributes = asList("ID", "other-column");
    otherValues = asList("3", "4");
    content = new CsvBuilder(";");
  }
  
  static Stream<Function<String, BaseVisumReader>> reader() {
    return Stream.of(BaseStreamReaderTest::newStreamReader, BaseStreamReaderTest::newCachedReader);
  }

  private static BaseVisumReader newStreamReader(String content) {
    return new StreamReader() {

      @Override
      protected BufferedReader createReader(File routesFile, Charset charset) throws IOException {
        return newBufferedReader(content);
      }

    };
  }
  
  private static BaseVisumReader newCachedReader(String content) {
    return new StreamReader() {
      
      @Override
      protected BufferedReader createReader(File routesFile, Charset charset) throws IOException {
        return newBufferedReader(content);
      }
      
    };
  }

  private static BufferedReader newBufferedReader(String content) {
    return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes())));
  }

  @ParameterizedTest
  @MethodSource("reader")
  void readsSingleTable(Function<String, BaseVisumReader> readerFactory) throws Exception {
    addSomeTable();

    BaseVisumReader reader = readerFactory.apply(content.toString());

    List<Row> rows = reader.read(dummyFile(), someTable).collect(toList());

    assertThat(rows, contains(Row.createRow(someValues, someAttributes)));
  }


  @ParameterizedTest
  @MethodSource("reader")
  void readsMultipleTables(Function<String, BaseVisumReader> readerFactory) throws Exception {
    addSomeTable();
    addOtherTable();

    BaseVisumReader reader = readerFactory.apply(content.toString());

    List<Row> someRows = reader.read(dummyFile(), someTable).collect(toList());
    List<Row> otherRows = reader.read(dummyFile(), otherTable).collect(toList());

    assertThat(someRows, contains(Row.createRow(someValues, someAttributes)));
    assertThat(otherRows, contains(Row.createRow(otherValues, otherAttributes)));
  }


  @ParameterizedTest
  @MethodSource("reader")
  void readsMissingTable(Function<String, BaseVisumReader> readerFactory) throws Exception {
    addSomeTable();
    BaseVisumReader reader = readerFactory.apply(content.toString());

    List<Row> rows = reader.read(dummyFile(), otherTable).collect(toList());

    assertThat(rows, is(empty()));
  }

  private void addOtherTable() {
    addTable(otherTable, otherValues, otherAttributes);
  }

  private void addSomeTable() {
    addTable(someTable, someValues, someAttributes);
  }

  private void addTable(String tableName, List<String> values, List<String> attributes) {
    content.append("$" + tableName + ":" + attributes.get(0));
    content.newLine(attributes.get(1));
    content.append(values.get(0));
    content.newLine(values.get(1));
    content.newLine("");
  }

  private static File dummyFile() {
    return new File("");
  }
}
