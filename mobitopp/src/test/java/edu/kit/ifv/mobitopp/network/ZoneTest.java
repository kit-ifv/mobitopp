package edu.kit.ifv.mobitopp.network;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder;
import edu.kit.ifv.mobitopp.visum.VisumRoadNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ZoneTest {

  private static final int areaId = 1;
  private VisumRoadNetwork visumNetwork;
  private SimpleRoadNetwork roadNetwork;

  @BeforeEach
  public void initialise() {
    visumNetwork = VisumBuilder
        .visumNetwork()
        .withDefaultCarSystem()
        .with(new VisumSurface(areaId, emptyList(), emptyList()))
        .build();
    roadNetwork = new SimpleRoadNetwork(visumNetwork);
  }

  @Test
  void isNotExternal() {
    assertAll(() -> assertTrue(createZoneWithId(100000).isExternal()),
        () -> assertTrue(createZoneWithId(699999).isExternal()));
  }

  @Test
  void isExternal() {
    assertFalse(createZoneWithId(700000).isExternal());
  }
  
  @Test
  void isNotOuter() {
    assertAll(() -> assertFalse(createZoneWithId(100000).isOuter()),
        () -> assertFalse(createZoneWithId(699999).isOuter()));
  }
  
  @Test
  void isOuter() {
    assertTrue(createZoneWithId(700000).isOuter());
  }
  
  private Zone createZoneWithId(int zoneId) {
    VisumZone vZone = VisumBuilder.visumZone().withId(zoneId).withArea(areaId).build();
    Zone zone = new Zone(roadNetwork, visumNetwork, vZone);
    return zone;
  }
}
