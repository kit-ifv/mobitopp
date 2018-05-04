package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;

public class VisumLinkType implements Serializable {

	private static final long serialVersionUID = 1L;

	public final int id;
	public final String name;
	public final VisumTransportSystemSet transportSystem;
	public final VisumLinkAttributes attributes;

	public VisumLinkType(
		int id,
		String name,
		VisumTransportSystemSet transportSystem,
		int numberOfLanes,
		int capacityCar,
		int freeFlowSpeedCar,
		int walkSpeed
	) {

		this.id = id;
		this.name = name;
		this.transportSystem = transportSystem;

		this.attributes = new VisumLinkAttributes(numberOfLanes, capacityCar, freeFlowSpeedCar,
				walkSpeed);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((transportSystem == null) ? 0 : transportSystem.hashCode());
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
		VisumLinkType other = (VisumLinkType) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (transportSystem == null) {
			if (other.transportSystem != null)
				return false;
		} else if (!transportSystem.equals(other.transportSystem))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VisumLinkType [id=" + id + ", name=" + name + ", transportSystem=" + transportSystem
				+ ", attributes=" + attributes + "]";
	}

}
