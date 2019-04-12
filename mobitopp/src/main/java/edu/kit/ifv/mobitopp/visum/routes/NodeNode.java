package edu.kit.ifv.mobitopp.visum.routes;

import java.util.Objects;

public class NodeNode {

  private final int from;
  private final int to;

  public NodeNode(int from, int to) {
    super();
    this.from = from;
    this.to = to;
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NodeNode other = (NodeNode) obj;
    return from == other.from && to == other.to;
  }

  @Override
  public String toString() {
    return "NodeNode [from=" + from + ", to=" + to + "]";
  }

}
