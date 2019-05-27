package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.visum.VisumLinkType;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;

public class VisumLinkTypeBuilder {

  private int id;
  private String name;
  private VisumTransportSystemSet transportSystemSet;
  private int numberOfLanes;
  private int capacityCar;
  private int freeFlowSpeed;
  private int walkSpeed;

  public VisumLinkTypeBuilder() {
    super();
    id = 0;
    name = "";
    numberOfLanes = 0;
    capacityCar = 0;
    freeFlowSpeed = 0;
    walkSpeed = 0;
  }

  public VisumLinkType build() {
    return new VisumLinkType(id, name, transportSystemSet, numberOfLanes, capacityCar,
        freeFlowSpeed, walkSpeed);
  }

  public VisumLinkTypeBuilder withId(int id) {
    this.id = id;
    return this;
  }

  public VisumLinkTypeBuilder with(VisumTransportSystemSet systems) {
    transportSystemSet = systems;
    return this;
  }

  public VisumLinkTypeBuilder withWalkSpeed(int speed) {
    this.walkSpeed = speed;
    return this;
  }
  
  public VisumLinkTypeBuilder withCarCapacity(int capacityCar) {
    this.capacityCar = capacityCar;
    return this;
  }
  
  public VisumLinkTypeBuilder withFreeFlowSpeedCar(int speed) {
    this.freeFlowSpeed = speed;
    return this;
  }
  
  public VisumLinkTypeBuilder withNumberOfLanes(int numberOfLanes) {
    this.numberOfLanes = numberOfLanes;
    return this;
  }

}
