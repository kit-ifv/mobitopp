package edu.kit.ifv.mobitopp.visum;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.kit.ifv.mobitopp.network.Node;

public class VisumNode 
	implements Node, Serializable
{
	private static final long serialVersionUID = 1L;

	private final int id;
	public final String name;
	public final int type;
	public final VisumPoint3 coord;

	private List<VisumTurn> turns;

	public VisumNode(int id, String name, int type, float coord_x, float coord_y, float coord_z) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.coord = new VisumPoint3(coord_x, coord_y, coord_z);

		this.turns = Collections.unmodifiableList(new ArrayList<>(0));
	}
	
	@Override
	public int id() {
		return id;
	}
	
	@Override
	public Point2D coordinate() {
		return coord.asPoint2D();
	}

	protected void setTurns(List<VisumTurn> turns) {
		this.turns = Collections.unmodifiableList(turns);
	}

	protected List<VisumTurn> getTurns() {
		return this.turns;
	}

	@Override
  public int hashCode() {
    return Objects.hash(coord, id, name, turns, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumNode other = (VisumNode) obj;
    return Objects.equals(coord, other.coord) && id == other.id && Objects.equals(name, other.name)
        && Objects.equals(turns, other.turns) && type == other.type;
  }

  public String toString() {
		return "VisumNode(" + id + "," + name + "," + type + "," + coord + "," + turns + ")";
	}

}
