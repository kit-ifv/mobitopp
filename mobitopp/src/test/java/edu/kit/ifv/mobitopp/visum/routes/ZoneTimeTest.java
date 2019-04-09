package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import nl.jqno.equalsverifier.EqualsVerifier;

public class ZoneTimeTest {

  @Test
  void withChangedTime() throws Exception {
    String zone = "Z1";
    RelativeTime time = RelativeTime.ofMinutes(1);
    RelativeTime extension = RelativeTime.ofMinutes(2);
    ZoneTime zoneTime = new ZoneTime(zone, time);
    
    ZoneTime extendedZoneTime = zoneTime.addTime(extension);
    RelativeTime extendedTime = time.plus(extension);
    assertThat(extendedZoneTime, is(equalTo(new ZoneTime(zone, extendedTime))));
  }
  
  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(ZoneTime.class).usingGetClass().verify();
  }
}
