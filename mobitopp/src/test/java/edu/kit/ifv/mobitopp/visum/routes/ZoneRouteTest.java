package edu.kit.ifv.mobitopp.visum.routes;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ZoneRouteTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(ZoneRoute.class).usingGetClass().verify();
  }
}
