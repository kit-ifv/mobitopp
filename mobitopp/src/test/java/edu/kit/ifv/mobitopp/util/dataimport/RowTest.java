package edu.kit.ifv.mobitopp.util.dataimport;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class RowTest {

  private static final String missingAttribute = "missing-attribute";
  private static final String attribute = "attribute";

  @Test
  void getValueAsNumber() throws Exception {
    List<String> values = asList(String.valueOf(1));
    List<String> attributes = asList(attribute);
    Row row = Row.createRow(values, attributes);
    
    assertAll( 
        () -> assertTrue(row.containsAttribute(attribute)),
        () -> assertThat(row.valueAsInteger(attribute), is(equalTo(1))),
        () -> assertThat(row.valueAsFloat(attribute), is(equalTo(1.0f))),
        () -> assertThat(row.valueAsDouble(attribute), is(equalTo(1.0d))));
  }
  
  @Test
	void fillsUpEmptyValues() throws Exception {
		Row row = Row.createRow(emptyList(), List.of(attribute));
		
		assertThat(row.get(attribute)).isEmpty();
	}

  @Test
	void ignoresMoreValuesThanAttributes() throws Exception {
		Row row = Row.createRow(List.of("1", "2"), List.of(attribute));
		
		assertThat(row.get(attribute)).isEqualTo("1");
	}
  
  @Test
  void failsForMissingValue() throws Exception {
    int expectedValue = 1;
    List<String> values = asList(String.valueOf(expectedValue));
    List<String> attributes = asList(attribute);
    Row row = Row.createRow(values, attributes);

    assertAll(() -> assertFalse(row.containsAttribute(missingAttribute)),
        () -> assertThrows(IllegalArgumentException.class, () -> row.get(missingAttribute)));
  }
  
  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(Row.class).usingGetClass().verify();
  }
}
