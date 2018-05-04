package edu.kit.ifv.mobitopp.visum;


import java.io.Serializable;

public class VisumTurn 
	implements Serializable
{
	
	private static final long serialVersionUID = 1L;

	public final VisumNode node;
	public final VisumNode from;
	public final VisumNode to;
	public final int type;
	public final VisumTransportSystemSet transportSystems;
	public final int capacity;
	public final int timePenaltyInSec;



	public VisumTurn(
		VisumNode node,
		VisumNode from,
		VisumNode to,
		int type,
		VisumTransportSystemSet transportSystems,
		int capacity,
		int timePenaltyInSec
	) {
		this.node=node;
		this.from=from;
		this.to=to;
		this.type=type;
		this.transportSystems=transportSystems;
		this.capacity=capacity;
		this.timePenaltyInSec=timePenaltyInSec;
	}

	public String toString() {
		return "VisumTurn(" + node.id() + ",from=" + from.id() + ",to=" + to.id() + ")";
	}
}
