package edu.kit.ifv.mobitopp.visum;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TableDescriptionTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(TableDescription.class).usingGetClass().verify();
  }
}
