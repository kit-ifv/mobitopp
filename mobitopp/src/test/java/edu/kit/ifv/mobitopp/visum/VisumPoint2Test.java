package edu.kit.ifv.mobitopp.visum;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VisumPoint2Test {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(VisumPoint2.class).usingGetClass().verify();
  }
}
