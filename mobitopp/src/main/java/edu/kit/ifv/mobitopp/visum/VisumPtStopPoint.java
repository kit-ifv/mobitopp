package edu.kit.ifv.mobitopp.visum;

import java.awt.geom.Point2D;
import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class VisumPtStopPoint
	implements Serializable
{

	public final int id;
	public final VisumPtStopArea stopArea;
	public final String code;
	public final String name;
	public final int type;
	public final VisumTransportSystemSet transportSystems;
	public final boolean directed;


	public VisumPtStopPoint (
		int id,
		VisumPtStopArea stopArea,
		String code,
		String name,
		int type,
		VisumTransportSystemSet transportSystems,
		boolean directed
	) {
		this.id = id;
		this.stopArea = stopArea;
		this.code = code;
		this.name = name;
		this.type = type;

		this.transportSystems = transportSystems;

		this.directed = directed;
	}
	
	public abstract Point2D coordinate();

	public String toString() {

		return "VisumPtStopPoint( "
						+ id + ", "
						+ stopArea.id + ", "
						+ code + ", "
						+ name + ", "
						+ type + ", "
						+ transportSystems + ", "
						+ directed + ")";
	}

	static class Node extends VisumPtStopPoint {
	
		public final VisumNode node;
	
		public Node (
			int id,
			VisumPtStopArea stopArea,
			String code,
			String name,
			int type,
			VisumTransportSystemSet transportSystems,
			boolean directed,
			VisumNode node
		) {
	
			super(id, stopArea, code, name, type, transportSystems, directed);
	
			this.node = node;
		}
	
		@Override
		public Point2D coordinate() {
			return node.coordinate();
		}
	
	}

	static class Link extends VisumPtStopPoint {
	
		public final VisumNode nodeBefore;
		public final VisumLink link;
		public final float relativePosition;
	
		public Link (
			int id,
			VisumPtStopArea stopArea,
			String code,
			String name,
			int type,
			VisumTransportSystemSet transportSystems,
			boolean directed,
			VisumNode nodeBefore,
			VisumLink link,
			float relativePosition
		) {
	
			super(id, stopArea, code, name, type, transportSystems, directed);
	
			this.nodeBefore = nodeBefore;
			this.link = link;
			this.relativePosition = relativePosition;
		}
		
		@Override
		public Point2D coordinate() {
			return nodeBefore.coordinate();
		}
	}
}
