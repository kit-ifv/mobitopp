package edu.kit.ifv.mobitopp.simulation.emobility;

import java.io.Serializable;

public class ChargingPower implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final ChargingPower zero = new ChargingPower(0);

	private final int watt;
	
	public ChargingPower(int watt) {
		super();
		this.watt = watt;
	}

	public static ChargingPower fromKw(double kiloWatt) {
		return new ChargingPower(Math.toIntExact(Math.round(kiloWatt * 1000.0d)));
	}
	
	public float inKw() {
		return watt / 1000.0f;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + watt;
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
		ChargingPower other = (ChargingPower) obj;
		if (watt != other.watt)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChargingPower [watt=" + watt + "]";
	}


}
