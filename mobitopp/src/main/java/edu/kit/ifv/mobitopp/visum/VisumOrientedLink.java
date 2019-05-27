package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;
import java.util.Objects;

public class VisumOrientedLink
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	public final String id;
	public final VisumNode from;
	public final VisumNode to;
	public final String name;
	public final VisumLinkType linkType;
	public final VisumTransportSystemSet transportSystems; 
	public final float length;
	public final VisumLinkAttributes attributes;


	public VisumOrientedLink(
		String id,
		VisumNode from,
		VisumNode to,
		String name,
		VisumLinkType linkType,
		VisumTransportSystemSet transportSystems,
		float length,
		VisumLinkAttributes attributes
	) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.name = name;
		this.linkType = linkType;
		this.transportSystems = transportSystems;
		this.length = length;
		this.attributes = attributes;
	}

	@Override
  public int hashCode() {
    return Objects.hash(attributes, from, id, length, linkType, name, to, transportSystems);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VisumOrientedLink other = (VisumOrientedLink) obj;
    return Objects.equals(attributes, other.attributes) && Objects.equals(from, other.from)
        && Objects.equals(id, other.id)
        && Float.floatToIntBits(length) == Float.floatToIntBits(other.length)
        && Objects.equals(linkType, other.linkType) && Objects.equals(name, other.name)
        && Objects.equals(to, other.to) && Objects.equals(transportSystems, other.transportSystems);
  }

  public String toString() {
		
		return "VisumOrientedLink(" 
							+ id + "," 
							+ from.id() + "," 
							+ to.id() + ","
							+ name + ","
							+ linkType.id + ","
							+ "'" + transportSystems + "'" + ","
							+ length + ","
							+ attributes.numberOfLanes + ","
							+ attributes.capacityCar + ","
							+ attributes.freeFlowSpeedCar 
							+ ")";
	}

}
