package edu.kit.ifv.mobitopp.visum.routes;

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

import edu.kit.ifv.mobitopp.visum.StreamReader;
import edu.kit.ifv.mobitopp.visum.reader.VisumFileReader;

public class BaseStreamReaderTest {

  private String someTable;
  private List<String> someAttributes;
  private List<String> someValues;
  private String otherTable;
  private List<String> otherAttributes;
  private List<String> otherValues;
  private TestTables testTables;

  @BeforeEach
  public void initialise() {
    testTables = new TestTables();
    someTable = testTables.someTable;
    someAttributes = testTables.someAttributes;
    someValues = testTables.someValues;
    otherTable = testTables.otherTable;
    otherAttributes = testTables.otherAttributes;
    otherValues = testTables.otherValues;
  }

  @ParameterizedTest
  @MethodSource("reader")
  void readsSingleTable(Function<String, VisumFileReader> readerFactory) throws Exception {
    testTables.addSomeTable();

    VisumFileReader reader = readerFactory.apply(testTables.toString());

    List<Row> rows = reader.read(dummyFile(), someTable).collect(toList());

    assertThat(rows, contains(Row.createRow(someValues, someAttributes)));
  }

  @ParameterizedTest
  @MethodSource("reader")
  void readsMultipleTables(Function<String, VisumFileReader> readerFactory) throws Exception {
    testTables.addSomeTable();
    testTables.addOtherTable();

    VisumFileReader reader = readerFactory.apply(testTables.toString());

    List<Row> someRows = reader.read(dummyFile(), someTable).collect(toList());
    List<Row> otherRows = reader.read(dummyFile(), otherTable).collect(toList());

    assertThat(someRows, contains(Row.createRow(someValues, someAttributes)));
    assertThat(otherRows, contains(Row.createRow(otherValues, otherAttributes)));
  }

  @ParameterizedTest
  @MethodSource("reader")
  void readsMissingTable(Function<String, VisumFileReader> readerFactory) throws Exception {
    testTables.addSomeTable();
    VisumFileReader reader = readerFactory.apply(testTables.toString());

    List<Row> rows = reader.read(dummyFile(), otherTable).collect(toList());

    assertThat(rows, is(empty()));
  }

  private static File dummyFile() {
    return new File("");
  }

  static Stream<Function<String, VisumFileReader>> reader() {
    return Stream.of(BaseStreamReaderTest::newStreamReader, BaseStreamReaderTest::newCachedReader);
  }

  private static VisumFileReader newStreamReader(String content) {
    return new StreamReader() {

      @Override
      protected BufferedReader createReader(File routesFile, Charset charset) throws IOException {
        return newBufferedReader(content);
      }

    };
  }

  private static VisumFileReader newCachedReader(String content) {
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
}
