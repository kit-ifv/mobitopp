package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ZoneIdTimeTest {

  @Test
  void withChangedTime() throws Exception {
    String zone = "Z1";
    RelativeTime time = RelativeTime.ofMinutes(1);
    RelativeTime extension = RelativeTime.ofMinutes(2);
    ZoneIdTime zoneTime = new ZoneIdTime(zone, time);
    
    ZoneIdTime extendedZoneTime = zoneTime.addTime(extension);
    RelativeTime extendedTime = time.plus(extension);
    assertThat(extendedZoneTime, is(equalTo(new ZoneIdTime(zone, extendedTime))));
  }
  
  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(ZoneIdTime.class).usingGetClass().verify();
  }
}
