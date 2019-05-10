package edu.kit.ifv.mobitopp.data.local.configuration;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TravelTimeMatrixIdTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(TravelTimeMatrixId.class).usingGetClass().verify();
  }
  
}
