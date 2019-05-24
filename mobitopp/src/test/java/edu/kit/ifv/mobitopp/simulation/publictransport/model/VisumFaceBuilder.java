package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.LinkedList;
import java.util.List;

import edu.kit.ifv.mobitopp.visum.VisumEdge;
import edu.kit.ifv.mobitopp.visum.VisumFace;

public class VisumFaceBuilder {

  private final int id;
  private final List<VisumEdge> edges;
  private final List<Integer> directions;

  public VisumFaceBuilder(int id) {
    super();
    this.id = id;
    edges = new LinkedList<>();
    directions = new LinkedList<>();
  }

  public VisumFace build() {
    return new VisumFace(id, edges, directions);
  }

  public VisumFaceBuilder with(VisumEdge edge, int direction) {
    edges.add(edge);
    directions.add(direction);
    return this;
  }
}
