package edu.kit.ifv.mobitopp.visum;

import static java.util.Collections.emptyList;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class VisumEdge implements Serializable {

  private static final long serialVersionUID = 1L;

  public final int id;
  public final VisumPoint from;
  public final VisumPoint to;
  public final List<VisumPoint> intermediate;

  public VisumEdge(int id, VisumPoint from, VisumPoint to, List<VisumPoint> intermediate) {
    this.id = id;
    this.from = from;
    this.to = to;
    this.intermediate = Collections.unmodifiableList(intermediate);
  }

  public VisumEdge(int id, VisumPoint from, VisumPoint to) {
    this(id, from, to, emptyList());
  }

  protected String intermediateToString() {
    String s = "";
    for (VisumPoint p : intermediate) {
      s += p + ",";
    }
    return s;
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, id, intermediate, to);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumEdge other = (VisumEdge) obj;
    return Objects.equals(from, other.from) && id == other.id
        && Objects.equals(intermediate, other.intermediate) && Objects.equals(to, other.to);
  }

  public String toString() {
    return id + ": (" + from + "->" + to + ":\n\t" + intermediateToString() + ")";
  }
}
