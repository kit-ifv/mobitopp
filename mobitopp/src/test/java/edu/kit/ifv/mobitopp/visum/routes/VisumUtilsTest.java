package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class VisumUtilsTest {

  @Test
  void convertSeconds() throws Exception {
    RelativeTime parsed = VisumUtils.parseTime("1s");
    
    assertThat(parsed, is(equalTo(RelativeTime.ofSeconds(1))));
  }
  
  @Test
  void convertMinutes() throws Exception {
    RelativeTime parsed = VisumUtils.parseTime("1m");
    
    assertThat(parsed, is(equalTo(RelativeTime.ofMinutes(1))));
  }
  
  @Test
  void convertOtherMinutes() throws Exception {
    RelativeTime parsed = VisumUtils.parseTime("1Min");
    
    assertThat(parsed, is(equalTo(RelativeTime.ofMinutes(1))));
  }
  
  @Test
  void convertHours() throws Exception {
    RelativeTime parsed = VisumUtils.parseTime("1h");
    
    assertThat(parsed, is(equalTo(RelativeTime.ofHours(1))));
  }
  
  @Test
  void parsesCombination() throws Exception {
    RelativeTime parsed = VisumUtils.parseTime("1h 2m 3s");
    
    assertThat(parsed, is(equalTo(RelativeTime.ofHours(1).plusMinutes(2).plusSeconds(3))));
  }
}
