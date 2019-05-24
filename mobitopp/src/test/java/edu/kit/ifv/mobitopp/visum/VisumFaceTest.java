package edu.kit.ifv.mobitopp.visum;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VisumFaceTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(VisumFace.class).usingGetClass().verify();
  }
}
