package edu.kit.ifv.mobitopp.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ZoneIdTest {

  @Test
  void comparesByMatrixColumn() throws Exception {
    String lowExternalId = "11";
    int lowMatrixColumn = 1;
    ZoneId lowId = new ZoneId(lowExternalId, lowMatrixColumn);
    String highExternalId = "22";
    int highMatrixColumn = 2;
    ZoneId highId = new ZoneId(highExternalId, highMatrixColumn);

    assertAll(() -> assertThat(lowId.compareTo(highId), is(lessThan(0))),
        () -> assertThat(highId.compareTo(lowId), is(greaterThan(0))),
        () -> assertThat(lowId.compareTo(lowId), is(0)));
  }

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(ZoneId.class).withIgnoredFields("externalId").usingGetClass().verify();
  }
}
