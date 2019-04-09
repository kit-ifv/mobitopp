package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class RouteBuilderTest {

  private static final String otherIntermediateId = "4";
  private static final String intermediateId = "3";
  private static final String destinationId = "2";
  private static final String originId = "1";
  private ZoneTime origin;
  private ZoneTime intermediateZone;
  private ZoneTime otherIntermediateZone;
  private ZoneTime destination;
  private RelativeTime start;
  private RelativeTime toIntermediate;
  private RelativeTime toOtherIntermediate;
  private RelativeTime toDestination;

  @BeforeEach
  public void initialise() {
    start = RelativeTime.ZERO;
    toIntermediate = RelativeTime.ofMinutes(1);
    toOtherIntermediate = RelativeTime.ofMinutes(2);
    toDestination = RelativeTime.ofMinutes(1);
    origin = new ZoneTime(originId, start);
    destination = new ZoneTime(destinationId, toDestination);
    intermediateZone = new ZoneTime(intermediateId, toIntermediate);
    otherIntermediateZone = new ZoneTime(otherIntermediateId, toOtherIntermediate);
  }

  @Test
  void buildsOdPair() throws Exception {
    RouteBuilder builder = new RouteBuilder(origin, destination);

    OdPair odPair = builder.buildOdPair();

    assertThat(odPair, is(equalTo(new OdPair(origin.zone(), destination.zone()))));
  }

  @Test
  void buildsRoute() throws Exception {
    RouteBuilder builder = new RouteBuilder(origin, destination);
    builder.addZone(origin);
    builder.addZone(intermediateZone);
    builder.addZone(destination);
    ZoneRoute route = builder.buildZoneRoute();

    ZoneTime intermediateZoneArrival = arriveAt(intermediateZone, start.plus(toIntermediate));
    assertThat(route, is(equalTo(new ZoneRoute(intermediateZoneArrival))));
  }

  private ZoneTime arriveAt(ZoneTime zone, RelativeTime time) {
    return new ZoneTime(zone.zone(), time);
  }

  @Test
  void buildsRouteWithSameZoneOnlyOnceInARow() throws Exception {
    ZoneTime intermediateZoneArrival = arriveAt(intermediateZone, start.plus(toIntermediate));
    RelativeTime secondArrivalAtIntermediate = start.plus(toIntermediate);
    ZoneTime arrivalAtDestination = arriveAt(destination,
        secondArrivalAtIntermediate.plus(toDestination));
    RouteBuilder builder = new RouteBuilder(origin, arrivalAtDestination);
    builder.addZone(origin);
    builder.addZone(intermediateZone);
    builder.addZone(intermediateZone);
    builder.addZone(destination);
    ZoneRoute route = builder.buildZoneRoute();

    assertThat(route, is(equalTo(new ZoneRoute(intermediateZoneArrival))));
  }

  @Test
  void buildsRouteWithSameZoneMultipleTimesWithOtherInBetween() throws Exception {
    RelativeTime arrival = start
        .plus(toIntermediate)
        .plus(toOtherIntermediate)
        .plus(toIntermediate)
        .plus(toDestination);
    ZoneTime atDestination = arriveAt(destination, arrival);
    RouteBuilder builder = new RouteBuilder(origin, atDestination);
    builder.addZone(origin);
    builder.addZone(intermediateZone);
    builder.addZone(otherIntermediateZone);
    builder.addZone(intermediateZone);
    builder.addZone(destination);
    ZoneRoute route = builder.buildZoneRoute();

    assertThat(route,
        is(equalTo(new ZoneRoute(intermediateZone, otherIntermediateZone, intermediateZone))));
  }

  @Test
  void buildsRouteWithSameZoneMultipleTimesAtStart() throws Exception {
    RelativeTime arrival = start
        .plus(toIntermediate)
        .plus(toOtherIntermediate)
        .plus(toIntermediate)
        .plus(toDestination);
    ZoneTime startAtIntermediate = arriveAt(intermediateZone, start);
    ZoneTime atDestination = arriveAt(destination, arrival);
    RouteBuilder builder = new RouteBuilder(startAtIntermediate, atDestination);
    builder.addZone(intermediateZone);
    builder.addZone(intermediateZone);
    builder.addZone(destination);
    ZoneRoute route = builder.buildZoneRoute();

    assertThat(route, is(equalTo(new ZoneRoute())));
  }
}
