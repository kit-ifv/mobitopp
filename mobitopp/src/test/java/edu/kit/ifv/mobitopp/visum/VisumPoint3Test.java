package edu.kit.ifv.mobitopp.visum;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VisumPoint3Test {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(VisumPoint3.class).usingGetClass().verify();
  }
}
