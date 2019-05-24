package edu.kit.ifv.mobitopp.visum;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VisumSurfaceTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier
        .forClass(VisumSurface.class)
        .withOnlyTheseFields("id", "faces", "enclave")
        .usingGetClass()
        .verify();
  }
}
