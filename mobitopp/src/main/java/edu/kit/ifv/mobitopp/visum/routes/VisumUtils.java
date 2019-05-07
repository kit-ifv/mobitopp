package edu.kit.ifv.mobitopp.visum.routes;

import java.time.Duration;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public final class VisumUtils {

  private VisumUtils() {
    super();
  }
  
  public static RelativeTime parseTime(String time) {
    if (time.isEmpty()) {
      return RelativeTime.ZERO;
    }
    String string = "PT" + time.toUpperCase().replaceAll("MIN", "M").replaceAll(" ", "");
    return RelativeTime.of(Duration.parse(string));
  }
}
