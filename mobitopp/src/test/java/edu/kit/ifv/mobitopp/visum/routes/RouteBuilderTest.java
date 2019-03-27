package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.visum.routes.OdPair;
import edu.kit.ifv.mobitopp.visum.routes.RouteBuilder;
import edu.kit.ifv.mobitopp.visum.routes.ZoneRoute;

public class RouteBuilderTest {

  private static final String origin = "1";
  private static final String destination = "2";
  private static final String intermediateZone = "3";
  private static final String otherIntermediateZone = "4";
  private RouteBuilder builder;

  @BeforeEach
  public void initialise() {
    builder = new RouteBuilder(origin, destination);
  }

  @Test
  void buildsOdPair() throws Exception {
    OdPair odPair = builder.buildOdPair();

    assertThat(odPair, is(equalTo(new OdPair(origin, destination))));
  }

  @Test
  void buildsRoute() throws Exception {
    builder.addZone(origin);
    builder.addZone(intermediateZone);
    builder.addZone(destination);
    ZoneRoute route = builder.buildZoneRoute();

    assertThat(route, is(equalTo(new ZoneRoute(intermediateZone))));
  }

  @Test
  void buildsRouteWithSameZoneOnlyOnceInARow() throws Exception {
    builder.addZone(origin);
    builder.addZone(intermediateZone);
    builder.addZone(intermediateZone);
    builder.addZone(destination);
    ZoneRoute route = builder.buildZoneRoute();

    assertThat(route, is(equalTo(new ZoneRoute(intermediateZone))));
  }
  
  @Test
  void buildsRouteWithSameZoneMultipleTimesWithOtherInBetween() throws Exception {
    builder.addZone(origin);
    builder.addZone(intermediateZone);
    builder.addZone(otherIntermediateZone);
    builder.addZone(intermediateZone);
    builder.addZone(destination);
    ZoneRoute route = builder.buildZoneRoute();
    
    assertThat(route, is(equalTo(new ZoneRoute(intermediateZone, otherIntermediateZone, intermediateZone))));
  }
}
