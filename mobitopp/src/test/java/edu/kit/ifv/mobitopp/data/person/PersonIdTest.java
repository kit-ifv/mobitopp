package edu.kit.ifv.mobitopp.data.person;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class PersonIdTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(PersonId.class).usingGetClass().verify();
  }
}
