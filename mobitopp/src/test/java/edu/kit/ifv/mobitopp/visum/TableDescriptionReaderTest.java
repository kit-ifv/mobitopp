package edu.kit.ifv.mobitopp.visum;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.visum.routes.TestTables;

public class TableDescriptionReaderTest {

  private static final String defaultSeparator = ";";
  private TestTables testTables;

  @BeforeEach
  public void initialise() {
    testTables = new TestTables();
  }
  
  @Test
  void readSingleTable() throws Exception {
    testTables.addSomeTable();
    
    TableDescriptionReader reader = new TableDescriptionReader(defaultSeparator);
    
    Map<String, TableDescription> tables = reader.readTables(content());
    
    TableDescription someDescription = new TableDescription(1, 2, testTables.someAttributes,
        defaultSeparator);
    assertThat(tables, hasEntry(testTables.someTable, someDescription));
  }
  
  @Test
  void readMultipleTables() throws Exception {
    testTables.addSomeTable();
    testTables.addCommentLine();
    testTables.addEmptyLine();
    testTables.addOtherTable();
    
    TableDescriptionReader reader = new TableDescriptionReader(defaultSeparator);
    
    Map<String, TableDescription> tables = reader.readTables(content());
    
    TableDescription someDescription = new TableDescription(1, 2, testTables.someAttributes,
        defaultSeparator);
    TableDescription otherDescription = new TableDescription(6, 7, testTables.otherAttributes,
        defaultSeparator);
    assertThat(tables, hasEntry(testTables.someTable, someDescription));
    assertThat(tables, hasEntry(testTables.otherTable, otherDescription));
  }

  private BufferedReader content() {
    return new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(testTables.toString().getBytes())));
  }
}
