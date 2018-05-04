package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;

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
		String direction,
		int type,
		VisumTransportSystemSet transportSystems,
		float length, 
		int travelTimeInSeconds
	) {
	
		this.zone = zone;
		this.node = node;
		this.direction = getDirection(direction);
		this.type = type;
		this.transportSystems = transportSystems;
		this.length = length;
		this.travelTimeInSeconds = travelTimeInSeconds;

		this.id = "C" + zone.id + "-" + node.id() + ":" + (this.direction == Direction.ORIGIN ? "O" : "D");
		
	}

	private static Direction getDirection(String direction) {

		if (!direction.equals("Q") && !direction.equals("Z")) {
			throw new IllegalArgumentException("VisumConnector: direction does not match (Q|Z)");
		}

		return direction.equals("Q") ? Direction.ORIGIN : Direction.DESTINATION;
	}

	public String toString() {

  	return "VisumConnector(\n" 
    	                  + "\t" + zone + ",\n"
      	                + "\t" + node + ",\n"
        	              + "\t" 	+ direction + ","
																+ type + "," 
																+ transportSystems + ","
																+ length + ","
																+ travelTimeInSeconds + "\n)";
	}




}
