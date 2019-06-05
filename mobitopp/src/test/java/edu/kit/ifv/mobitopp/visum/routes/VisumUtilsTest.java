package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.time.Duration;

import org.junit.jupiter.api.Test;

public class VisumUtilsTest {

  private static final double nanoSecondsPerSecond = 1e9d;

  @Test
  void convertNanoSeconds() throws Exception {
    Duration parsed = VisumUtils.parseTime("0.5s");

    Double nanoSeconds = 0.5 * nanoSecondsPerSecond;
    assertThat(parsed, is(equalTo(Duration.ofNanos(nanoSeconds.longValue()))));
  }

  @Test
  void convertSeconds() throws Exception {
    Duration parsed = VisumUtils.parseTime("1s");

    assertThat(parsed, is(equalTo(Duration.ofSeconds(1))));
  }

  @Test
  void convertMinutes() throws Exception {
    Duration parsed = VisumUtils.parseTime("1m");

    assertThat(parsed, is(equalTo(Duration.ofMinutes(1))));
  }

  @Test
  void convertOtherMinutes() throws Exception {
    Duration parsed = VisumUtils.parseTime("1Min");

    assertThat(parsed, is(equalTo(Duration.ofMinutes(1))));
  }

  @Test
  void convertHours() throws Exception {
    Duration parsed = VisumUtils.parseTime("1h");

    assertThat(parsed, is(equalTo(Duration.ofHours(1))));
  }

  @Test
  void parsesCombination() throws Exception {
    Duration parsed = VisumUtils.parseTime("1h 2m 3s");

    assertThat(parsed, is(equalTo(Duration.ofHours(1).plusMinutes(2).plusSeconds(3))));
  }

  @Test
  void parsesEmptyString() throws Exception {
    Duration parsed = VisumUtils.parseTime("");

    assertThat(parsed, is(equalTo(Duration.ZERO)));
  }
}
