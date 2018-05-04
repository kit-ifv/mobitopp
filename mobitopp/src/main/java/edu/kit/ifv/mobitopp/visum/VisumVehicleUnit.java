package edu.kit.ifv.mobitopp.visum;


public class VisumVehicleUnit {

	public final int id;

	public final String code;
	public final String name;

	public final VisumTransportSystemSet transportSystems;

	public final int capacity;
	public final int seats;



	public VisumVehicleUnit(
		int id,
		String code,
		String name,
		VisumTransportSystemSet transportSystems,
		int capacity,
		int seats
	) {
			this.id=id;
			this.code=code;
			this.name=name;

			this.capacity=capacity;
			this.seats=seats;

			this.transportSystems = transportSystems;
	}

	public String toString() {
		return "VisumVehicleUnit("
					+ id + ", "
					+ code + ", "
					+ name + ", "
					+ transportSystems + ", "
					+ capacity + ", "
					+ seats 
					+ ")";
	}

	public float getLength() {
		return 50.0f;
	}
}
