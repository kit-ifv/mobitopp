package edu.kit.ifv.mobitopp.visum.routes;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class StateTest {

  @Test
  void parsesZoneTime() throws Exception {
    String zone = "Z1";
    RelativeTime time = RelativeTime.ofMinutes(1).plusSeconds(2);
    String serialised = serialised(time);
    Row row = Row
        .createRow(asList("Z1", serialised),
            asList("STRECKE\\NACHKNOTEN\\BEZIRKNR", "STRECKE\\T0-IVSYS(P)"));
    RouteReader routeReader = mock(RouteReader.class);
    State.intermediateZone.parse(row, routeReader);

    verify(routeReader).addZone(new ZoneIdTime(zone, time));
  }

  private String serialised(RelativeTime time) {
    long secondsInMinute = time.seconds() % 60;
    return String.format("%sm %ss", time.toMinutes(), secondsInMinute);
  }
}
