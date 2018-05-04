package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;

public class VisumLinkAttributes implements Serializable {

	private static final long serialVersionUID = 1L;

	public final int numberOfLanes;
	public final int capacityCar;
	public final int freeFlowSpeedCar;
	public final int walkSpeed;

	public VisumLinkAttributes(
		int numberOfLanes,
		int capacityCar,
		int freeFlowSpeedCar,
		int walkSpeed
	) {

		this.numberOfLanes = numberOfLanes;
		this.capacityCar = capacityCar;
		this.freeFlowSpeedCar = freeFlowSpeedCar;
		this.walkSpeed = walkSpeed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacityCar;
		result = prime * result + freeFlowSpeedCar;
		result = prime * result + numberOfLanes;
		result = prime * result + walkSpeed;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VisumLinkAttributes other = (VisumLinkAttributes) obj;
		if (capacityCar != other.capacityCar)
			return false;
		if (freeFlowSpeedCar != other.freeFlowSpeedCar)
			return false;
		if (numberOfLanes != other.numberOfLanes)
			return false;
		if (walkSpeed != other.walkSpeed)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VisumLinkAttributes [numberOfLanes=" + numberOfLanes + ", capacityCar=" + capacityCar
				+ ", freeFlowSpeedCar=" + freeFlowSpeedCar + ", walkSpeed=" + walkSpeed + "]";
	}

}
