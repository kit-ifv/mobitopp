package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class RouteBuilderTest {

  private static final String otherIntermediateId = "4";
  private static final String intermediateId = "3";
  private static final String destinationId = "2";
  private static final String originId = "1";
  private ZoneIdTime origin;
  private ZoneIdTime intermediateZone;
  private ZoneIdTime otherIntermediateZone;
  private ZoneIdTime destination;
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
    origin = new ZoneIdTime(originId, start);
    destination = new ZoneIdTime(destinationId, toDestination);
    intermediateZone = new ZoneIdTime(intermediateId, toIntermediate);
    otherIntermediateZone = new ZoneIdTime(otherIntermediateId, toOtherIntermediate);
  }

  @Test
  void buildsOdPair() throws Exception {
    RouteBuilder builder = new RouteBuilder(origin, destination);

    OdPair odPair = builder.buildOdPair();

    assertThat(odPair, is(equalTo(new OdPair(origin.zone(), destination.zone()))));
  }

  @Test
  void buildsRoute() throws Exception {
    RouteBuilder builder = createBuilder(origin, destination, origin, intermediateZone,
        destination);
    ZoneRoute route = builder.buildZoneRoute();

    ZoneIdTime intermediateZoneArrival = arriveAt(intermediateZone, start.plus(toIntermediate));
    assertThat(route, is(equalTo(new ZoneRoute(intermediateZoneArrival, destination))));
  }

  private RouteBuilder createBuilder(
      ZoneIdTime origin, ZoneIdTime destination, ZoneIdTime... zones) {
    RouteBuilder builder = new RouteBuilder(origin, destination);
    Arrays.stream(zones).forEach(z -> builder.addZone(z.zone(), z.time().toDuration()));
    return builder;
  }

  private ZoneIdTime arriveAt(ZoneIdTime zone, RelativeTime time) {
    return new ZoneIdTime(zone.zone(), time);
  }

  @Test
  void buildsRouteWithSameZoneOnlyOnceInARow() throws Exception {
    ZoneIdTime intermediateZoneArrival = arriveAt(intermediateZone, start.plus(toIntermediate));
    RelativeTime secondArrivalAtIntermediate = start.plus(toIntermediate);
    ZoneIdTime arrivalAtDestination = arriveAt(destination,
        secondArrivalAtIntermediate.plus(toDestination));
    RouteBuilder builder = createBuilder(origin, destination, origin, intermediateZone,
        intermediateZone, destination);
    ZoneRoute route = builder.buildZoneRoute();

    assertThat(route, is(equalTo(new ZoneRoute(intermediateZoneArrival, arrivalAtDestination))));
  }

  @Test
  void buildsRouteWithSameZoneMultipleTimesWithOtherInBetween() throws Exception {
    RelativeTime arrival = start
        .plus(toIntermediate)
        .plus(toOtherIntermediate)
        .plus(toIntermediate)
        .plus(toDestination);
    ZoneIdTime atDestination = arriveAt(destination, arrival);
    RouteBuilder builder = createBuilder(origin, atDestination, origin, intermediateZone,
        otherIntermediateZone, intermediateZone, destination);
    ZoneRoute route = builder.buildZoneRoute();

    assertThat(route, is(equalTo(
        new ZoneRoute(intermediateZone, otherIntermediateZone, intermediateZone, destination))));
  }

  @Test
  void buildsRouteWithSameZoneMultipleTimesAtStart() throws Exception {
    RelativeTime enterDestinationTime = start.plus(toIntermediate).plus(toIntermediate);
    RelativeTime arrival = enterDestinationTime.plus(toDestination);
    ZoneIdTime startAtIntermediate = arriveAt(intermediateZone, start);
    ZoneIdTime enterDestinationZone = arriveAt(destination, enterDestinationTime);
    ZoneIdTime atDestination = arriveAt(destination, arrival);
    RouteBuilder builder = createBuilder(startAtIntermediate, atDestination, intermediateZone,
        intermediateZone, destination);
    ZoneRoute route = builder.buildZoneRoute();

    assertThat(route, is(equalTo(new ZoneRoute(enterDestinationZone))));
  }
}
