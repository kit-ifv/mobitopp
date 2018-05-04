package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;

public class VisumCarSharingStation implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum engineType {
		UNKNOWN, CONVENTIONAL, ELECTRIC
	};

	public final int id;
	public final String name;
	public final VisumPoint2 coord;
	public final int numberOfCars;
	public final String parkingSpace;
	public final engineType engine;

	public VisumCarSharingStation(
			int id, String name, float coord_x, float coord_y, int numberOfCars, String parkingSpace,
			String type) {

		this.id = id;
		this.name = name;
		this.coord = new VisumPoint2(coord_x, coord_y);
		this.numberOfCars = numberOfCars;
		this.parkingSpace = parkingSpace;

		if (type.equalsIgnoreCase("normal")) {
			this.engine = engineType.CONVENTIONAL;
		} else if (type.equalsIgnoreCase("elektro")) {
			this.engine = engineType.ELECTRIC;
		} else {
			this.engine = engineType.UNKNOWN;

			throw new AssertionError("type=" + type);
		}

	}

}
