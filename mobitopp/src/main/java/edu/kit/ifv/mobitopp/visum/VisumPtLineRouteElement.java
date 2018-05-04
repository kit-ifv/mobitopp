package edu.kit.ifv.mobitopp.visum;

public class VisumPtLineRouteElement {

	public final VisumPtLineRoute lineRoute;

	public final int index;

	public final boolean isRoutePoint;
	public final VisumNode node;
	public final VisumPtStopPoint stopPoint;


	public final float lengthAfter;

	public VisumPtLineRouteElement(
		VisumPtLineRoute lineRoute,
		int index,
		boolean isRoutePoint,
		VisumNode node,
		VisumPtStopPoint stopPoint,
		float lengthAfter
	) {

		this.lineRoute = lineRoute;
		this.index = index;
		this.isRoutePoint = isRoutePoint;
		this.node = node;
		this.stopPoint = stopPoint;
		this.lengthAfter = lengthAfter;
	}

	public boolean isStop() {

		return this.stopPoint != null;
	}


	public String toString() {

		return "VisumPtLineRouteElement(" 
									+ lineRoute + ","
									+ index + ","
									+ isRoutePoint + ","
									+ node + ","
									+ stopPoint + ","
									+ lengthAfter + ")";
	}

}

