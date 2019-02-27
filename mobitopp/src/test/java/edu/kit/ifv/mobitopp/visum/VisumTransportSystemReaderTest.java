package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    reader = new VisumTransportSystemReader(table, language);
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

    assertThrows(IllegalArgumentException.class, () -> readTransportSystems());
  }

  private VisumTransportSystems readTransportSystems() {
    return reader.readTransportSystems();
  }

  private List<String> attributes() {
    return asList(StandardAttributes.code, StandardAttributes.name, StandardAttributes.type)
        .stream()
        .map(language::resolve)
        .collect(toList());
  }

}
