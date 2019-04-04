package edu.kit.ifv.mobitopp.visum.routes;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class RowTest {

  private static final String attribute = "attribute";

  @Test
  void getInteger() throws Exception {
    int expectedValue = 1;
    List<String> values = asList(String.valueOf(expectedValue));
    List<String> attributes = asList(attribute);
    Row row = Row.createRow(values, attributes);
    
    int value = row.valueAsInteger(attribute);
    
    assertThat(value, is(equalTo(expectedValue)));
  }
  
  @Test
  void failsForMissingValue() throws Exception {
    int expectedValue = 1;
    List<String> values = asList(String.valueOf(expectedValue));
    List<String> attributes = asList(attribute);
    Row row = Row.createRow(values, attributes);
    
    assertThrows(IllegalArgumentException.class, () -> row.get("missing-attribute"));
  }
  
  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(Row.class).usingGetClass().verify();
  }
}
