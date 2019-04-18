package edu.kit.ifv.mobitopp.data;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ZoneIdTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(ZoneId.class).usingGetClass().verify();
  }
}
