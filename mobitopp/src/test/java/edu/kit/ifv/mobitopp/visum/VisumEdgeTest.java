package edu.kit.ifv.mobitopp.visum;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VisumEdgeTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(VisumEdge.class).usingGetClass().verify();
  }
}
