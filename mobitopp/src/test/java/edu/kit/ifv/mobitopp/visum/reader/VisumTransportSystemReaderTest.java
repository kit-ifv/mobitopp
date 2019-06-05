package edu.kit.ifv.mobitopp.visum.reader;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;
import edu.kit.ifv.mobitopp.visum.VisumTable;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystem;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
import edu.kit.ifv.mobitopp.visum.reader.VisumTransportSystemReader;
import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumTransportSystemReaderTest {

  private static final String code = "code-1";
  private static final String name = "name-1";
  private static final String type = "typ-1";
  private static final String tableName = "tableName";

  private NetfileLanguage language;
  private VisumTable table;
  private VisumTransportSystemReader reader;

  @BeforeEach
  public void initialise() {
    language = StandardNetfileLanguages.german();
    table = new VisumTable(tableName, attributes());
    reader = new VisumTransportSystemReader(language);
  }

  @Test
  public void readEmptySystems() {
    VisumTransportSystems systems = readTransportSystems();

    assertTrue(systems.isEmpty());
  }

  @Test
  public void readSystems() {
    addTransportSystem();

    VisumTransportSystems systems = readTransportSystems();

    VisumTransportSystem expected = new VisumTransportSystem(code, name, type);
    assertThat(systems.getBy(code), is(equalTo(expected)));
  }

  private void addTransportSystem() {
    table.addRow(asList(code, name, type));
  }

  @Test
  public void readDuplicateSystems() {
    addTransportSystem();
    addTransportSystem();

    assertThrows(IllegalStateException.class, () -> readTransportSystems());
  }

  private VisumTransportSystems readTransportSystems() {
    return reader.readTransportSystems(content());
  }

  private Stream<Row> content() {
    return table.rows();
  }

  private List<String> attributes() {
    return asList(StandardAttributes.code, StandardAttributes.name, StandardAttributes.type)
        .stream()
        .map(language::resolve)
        .collect(toList());
  }

}
