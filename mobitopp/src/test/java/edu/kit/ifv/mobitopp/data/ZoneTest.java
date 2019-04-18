package edu.kit.ifv.mobitopp.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class ZoneTest {

  @Test
  void getId() throws Exception {
    int oid = 1;
    String id = "Z12";
    Zone someZone = ExampleZones.zoneWithId(id, oid);
    
    assertThat(someZone.getId(), is(equalTo(id)));
  }
}
