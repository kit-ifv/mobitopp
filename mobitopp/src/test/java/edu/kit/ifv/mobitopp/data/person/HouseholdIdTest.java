package edu.kit.ifv.mobitopp.data.person;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class HouseholdIdTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(HouseholdId.class).usingGetClass().verify();
  }
}
