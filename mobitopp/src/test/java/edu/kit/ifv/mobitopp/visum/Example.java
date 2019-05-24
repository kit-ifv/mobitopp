package edu.kit.ifv.mobitopp.visum;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumFace;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNode;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumTurn;
import static java.util.Collections.singletonMap;

import java.util.Map;

public class Example {

  public static VisumNetwork createVisumNetwork() {
    VisumTransportSystem bus = new VisumTransportSystem("B", "Bus", "OV");
    VisumTransportSystemSet systems = VisumTransportSystemSet
        .getByCode("B", new VisumTransportSystems(singletonMap("B", bus)));
    Map<VisumVehicleUnit, Integer> vehicles = singletonMap(
        new VisumVehicleUnit(1, "BUS", "Bus", systems, 0, 0), 1);
    
    VisumPoint point2 = new VisumPoint(933807.0,6269827.0);
    VisumPoint point7 = new VisumPoint(933909.0,6269852.0);
    VisumPoint point8 = new VisumPoint(933921.0,6269797.0);
    VisumPoint point9 = new VisumPoint(934070.0,6269801.0);
    VisumNode node1 = visumNode().withId(1).withName("").at(933836.0, 6269823.0).build();
    VisumNode node2 = visumNode().withId(2).withName("").at(934027.0, 6269817.0).build();
    return visumNetwork()
        .with(bus)
        .with(new VisumTransportSystem("F", "Fuss", "OVFuss"))
        .with(new VisumTransportSystem("P", "PKW", "IV"))
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
        .add(visumTurn().with(node1).from(node1).to(node1).build())
        .build();
  }

}
