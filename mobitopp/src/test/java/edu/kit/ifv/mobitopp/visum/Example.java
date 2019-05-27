package edu.kit.ifv.mobitopp.visum;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumConnector;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumFace;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumLink;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumLinkType;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumTurn;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumZoneBuilder;

public class Example {

  public static VisumNetwork createVisumNetwork() {
    VisumTransportSystem bus = new VisumTransportSystem("B", "Bus", "OV");
    VisumTransportSystem walk = new VisumTransportSystem("F", "Fuss", "OVFuss");
    VisumTransportSystem car = new VisumTransportSystem("P", "PKW", "IV");
    VisumTransportSystemSet systems = getTransportSystemsFor(bus);
    VisumTransportSystemSet turnSystems = getTransportSystemsFor(bus, car);
    VisumTransportSystemSet connectorSystems = getTransportSystemsFor(walk, car);
    VisumTransportSystemSet allSystems = getTransportSystemsFor(bus, walk, car);
    Map<VisumVehicleUnit, Integer> vehicles = singletonMap(
        new VisumVehicleUnit(1, "BUS", "Bus", systems, 0, 0), 1);
    
    VisumPoint point2 = new VisumPoint(933807.0d, 6269827.0d);
    VisumPoint point7 = new VisumPoint(933909.0d, 6269852.0d);
    VisumPoint point8 = new VisumPoint(933921.0d, 6269797.0d);
    VisumPoint point9 = new VisumPoint(934070.0d, 6269801.0d);
    VisumNode node1 = visumNode().withId(1).withName("").at(933836.0d, 6269823.0d).build();
    VisumNode node2 = visumNode().withId(2).withName("").at(934027.0d, 6269817.0d).build();
    VisumTurn turn1 = visumTurn().with(node1).from(node2).to(node2).with(turnSystems).build();
    VisumTurn turn2 = visumTurn().with(node2).from(node1).to(node1).with(turnSystems).build();
    node1.setTurns(singletonList(turn1));
    node2.setTurns(singletonList(turn2));
    VisumZone zone1 = createZoneBuilder()
        .withId(1)
        .withName("1")
        .withCoordinates(933861.0f, 6269829.0f)
        .withArea(1)
        .build();
    VisumZone zone2 = createZoneBuilder()
        .withId(2)
        .withName("2")
        .withCoordinates(934027.0f, 6269828.0f)
        .withArea(3)
        .build();
    VisumLinkType linkType = visumLinkType()
        .withId(0)
        .with(allSystems)
        .withNumberOfLanes(1)
        .withCarCapacity(99999)
        .withFreeFlowSpeedCar(50)
        .withWalkSpeed(4)
        .build();
    VisumLink link1 = visumLink()
        .withId(1)
        .from(node1)
        .to(node2)
        .with(allSystems)
        .withName("")
        .withLength(0.12543100882f)
        .withNumberOfLanes(1)
        .withCapacity(99999)
        .withFreeFlowSpeed(50)
        .with(linkType)
        .build();
    return visumNetwork()
        .with(bus)
        .with(walk)
        .with(car)
        .with(new VisumVehicleCombination(1, "BUS", "Bus", vehicles))
        .with(visumSurface()
            .withId(1)
            .withFace(visumFace(1)
                .with(new VisumEdge(1, point7, point2), 0)
                .with(new VisumEdge(2, point2, point8), 0)
                .with(new VisumEdge(3, point8, point7), 0)
                .build(), 0)
            .build())
        .with(visumSurface()
            .withId(3)
            .withFace(visumFace(3)
                .with(new VisumEdge(7, point7, point8), 0)
                .with(new VisumEdge(8, point8, point9), 0)
                .with(new VisumEdge(9, point9, point7), 0)
                .build(), 0)
            .build())
        .with(node1)
        .with(node2)
        .add(turn1)
        .add(turn2)
        .with(zone1)
        .with(zone2)
        .with(linkType)
        .with(link1)
        .addConnector(zone1,
            visumConnector()
                .with(zone1)
                .with(node1)
                .withLength(0.01652604391f)
                .withType(0)
                .origin()
                .with(connectorSystems)
                .build())
        .addConnector(zone1,
            visumConnector()
                .with(zone1)
                .with(node1)
                .withLength(0.01652604391f)
                .withType(0)
                .destination()
                .with(connectorSystems)
                .build())
        .addConnector(zone2,
            visumConnector()
                .with(zone2)
                .with(node2)
                .withLength(0.007276853964f)
                .withType(0)
                .origin()
                .with(connectorSystems)
                .build())
        .addConnector(zone2,
            visumConnector()
                .with(zone2)
                .with(node2)
                .withLength(0.007276853964f)
                .withType(0)
                .destination()
                .with(connectorSystems)
                .build())
        .build();
  }

  public static VisumZoneBuilder createZoneBuilder() {
    return visumZone()
        .withMainZone(0)
        .withType(0)
        .withParkingPlaces(0)
        .withChargingFacilities(0)
        .withFloatingCarCompany("")
        .withFloatingCarArea(0)
        .withFloatingCarNumber(0)
        .withPrivateChargingProbability(1.0f)
        .withCarSharingCarDensities(carSharingDensities())
        .withInnerZonePublicTransport(0.0f);
  }

  public static Map<String, Float> carSharingDensities() {
    return Stream.of("Car2Go", "Stadtmobil", "Flinkster").collect(toMap(Function.identity(), e -> 0.0f));
  }

  public static VisumTransportSystemSet getTransportSystemsFor(VisumTransportSystem... systems) {
    Map<String, VisumTransportSystem> set = Arrays.stream(systems).collect(toMap(s -> s.code, Function.identity()));
    String code = Arrays.stream(systems).map(s -> s.code).collect(joining(","));
    return VisumTransportSystemSet.getByCode(code, new VisumTransportSystems(set));
  }

}
