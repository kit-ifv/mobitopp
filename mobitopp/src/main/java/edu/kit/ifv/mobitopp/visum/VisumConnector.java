package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Objects;

public class VisumConnector 
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public static enum Direction { ORIGIN, DESTINATION }

	public final VisumZone zone;
	public final VisumNode node;
	public final Direction direction;
	public final int type;
	public final VisumTransportSystemSet transportSystems;
	public float length;
	public final String id;
	public final int travelTimeInSeconds;


	public VisumConnector(
		VisumZone zone,
		VisumNode node,
		Direction direction,
		int type,
		VisumTransportSystemSet transportSystems,
		float length, 
		int travelTimeInSeconds
	) {
	
		this.zone = zone;
		this.node = node;
		this.direction = direction;
		this.type = type;
		this.transportSystems = transportSystems;
		this.length = length;
		this.travelTimeInSeconds = travelTimeInSeconds;

		this.id = "C" + zone.id + "-" + node.id() + ":" + (this.direction == Direction.ORIGIN ? "O" : "D");
		
	}

	@Override
  public int hashCode() {
    return Objects
        .hash(direction, id, length, node, transportSystems, travelTimeInSeconds, type, zone);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumConnector other = (VisumConnector) obj;
    return direction == other.direction && Objects.equals(id, other.id)
        && Float.floatToIntBits(length) == Float.floatToIntBits(other.length)
        && Objects.equals(node, other.node)
        && Objects.equals(transportSystems, other.transportSystems)
        && travelTimeInSeconds == other.travelTimeInSeconds && type == other.type
        && Objects.equals(zone, other.zone);
  }

  @Override
  public String toString() {
    return "VisumConnector [zone=" + zone.id + ", node=" + node.id() + ", direction=" + direction
        + ", type=" + type + ", transportSystems=" + transportSystems + ", length=" + length
        + ", id=" + id + ", travelTimeInSeconds=" + travelTimeInSeconds + "]";
  }




}
