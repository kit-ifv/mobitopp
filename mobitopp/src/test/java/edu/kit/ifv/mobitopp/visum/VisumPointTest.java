package edu.kit.ifv.mobitopp.visum;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VisumPointTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(VisumPoint.class).usingGetClass().verify();
  }
}
