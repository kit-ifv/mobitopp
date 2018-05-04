package edu.kit.ifv.mobitopp.visum;


public class VisumPtStopArea {

	public final int id;
	public final VisumPtStop stop;
	public final String code;
	public final String name;
	public final VisumNode node;
	public final int type;
	public final VisumPoint2 coord;


	public VisumPtStopArea (
		int id,
		VisumPtStop stop,
		String code,
		String name,
		VisumNode node,
		int type,
		float coord_x,
		float coord_y
	) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.type = type;
		this.coord = new VisumPoint2(coord_x, coord_y);
		this.stop=stop;
		this.node=node;
	}

	public String toString() {

		return "VisumPtStopArea("
					+ stop.id + ","
					+ code + ","
					+ name + ","
					+ node + ","
					+ type + ","
					+ coord + ")";
	}


}
