package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;

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
