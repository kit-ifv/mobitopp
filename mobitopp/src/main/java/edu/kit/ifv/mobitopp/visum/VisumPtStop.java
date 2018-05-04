package edu.kit.ifv.mobitopp.visum;


public class VisumPtStop {

	public final int id;
	public final String code;
	public final String name;
	public final int type;
	public final VisumPoint2 coord;


	public VisumPtStop (
		int id,
		String code,
		String name,
		int type,
		float coord_x,
		float coord_y
	) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.type = type;
		this.coord = new VisumPoint2(coord_x, coord_y);
	}

	public String toString() {

		return "VisumPtStop("
						+ id + ", "
						+ code + ", "
						+ name + ", "
						+ type + ", "
						+ coord + ")";
	}


}
