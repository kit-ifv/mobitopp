package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTurn;

public class VisumTurnBuilder {

  private VisumNode node;
  private VisumNode from;
  private VisumNode to;
  private int type;
  private VisumTransportSystemSet transportSystems;
  private int capacity;
  private int timePenaltyInSec;
  
  public VisumTurnBuilder() {
    type = 4;
    capacity = 99999;
    timePenaltyInSec = 0;
  }
  
  public VisumTurn build() {
    return new VisumTurn(node, from, to, type, transportSystems, capacity, timePenaltyInSec);
  }

  public VisumTurnBuilder with(VisumNode node) {
    this.node = node;
    return this;
  }

  public VisumTurnBuilder from(VisumNode from) {
    this.from = from;
    return this;
  }
  
  public VisumTurnBuilder to(VisumNode to) {
    this.to = to;
    return this;
  }
  
  public VisumTurnBuilder with(VisumTransportSystemSet transportSystems) {
    this.transportSystems = transportSystems;
    return this;
  }
  
}
