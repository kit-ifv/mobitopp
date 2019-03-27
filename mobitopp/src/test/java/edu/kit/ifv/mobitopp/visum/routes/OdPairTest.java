package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.visum.routes.OdPair;
import nl.jqno.equalsverifier.EqualsVerifier;

public class OdPairTest {

  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(OdPair.class).usingGetClass().verify();
  }

  @Test
  void compare() throws Exception {
    OdPair lowOrigin = new OdPair("1", "1");
    OdPair highOrigin = new OdPair("2", "1");
    OdPair lowDestination = new OdPair("1", "1");
    OdPair highDestination = new OdPair("1", "2");

    assertAll(() -> assertThat("low origin, high origin", lowOrigin.compareTo(highOrigin), is(lessThan(0))),
        () -> assertThat("high origin, low origin", highOrigin.compareTo(lowOrigin), is(greaterThan(0))),
        () -> assertThat("low destination, high destination", lowDestination.compareTo(highDestination), is(lessThan(0))),
        () -> assertThat("high destination, low destination", highDestination.compareTo(lowDestination), is(greaterThan(0))),
        () -> assertThat("low origin", lowOrigin.compareTo(lowOrigin), is(0)),
        () -> assertThat("high origin", highOrigin.compareTo(highOrigin), is(0)),
        () -> assertThat("low destination", lowDestination.compareTo(lowDestination), is(0)),
        () -> assertThat("high destination", highDestination.compareTo(highDestination), is(0)));
  }
}
