package edu.kit.ifv.mobitopp.dataimport;

import java.io.Serializable;

import edu.kit.ifv.mobitopp.simulation.emobility.ChargingPower;
import edu.kit.ifv.mobitopp.util.ParameterFileParser;

public class DefaultPower implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final DefaultPower zero = new DefaultPower();

	private final Double CHARGING_POWER_KW_PUBLIC = null;
	private final Double CHARGING_POWER_KW_SEMIPUBLIC = null;
	private final Double CHARGING_POWER_KW_PRIVATE = null;

	private final ChargingPower publicFacility;
	private final ChargingPower semipublicFacility;
	private final ChargingPower privateFacility;

	private DefaultPower() {
		super();
		this.publicFacility = ChargingPower.zero;
		this.semipublicFacility = ChargingPower.zero;
		this.privateFacility = ChargingPower.zero;
	}

	public DefaultPower(String configFile) {
		super();
		new ParameterFileParser().parseConfig(configFile, this);
		publicFacility = ChargingPower.fromKw(CHARGING_POWER_KW_PUBLIC);
		semipublicFacility = ChargingPower.fromKw(CHARGING_POWER_KW_SEMIPUBLIC);
		privateFacility = ChargingPower.fromKw(CHARGING_POWER_KW_PRIVATE);
	}

	public ChargingPower publicFacility() {
		return publicFacility;
	}

	public ChargingPower semipublicFacility() {
		return semipublicFacility;
	}

	public ChargingPower privateFacility() {
		return privateFacility;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((privateFacility == null) ? 0 : privateFacility.hashCode());
		result = prime * result + ((publicFacility == null) ? 0 : publicFacility.hashCode());
		result = prime * result + ((semipublicFacility == null) ? 0 : semipublicFacility.hashCode());
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
		DefaultPower other = (DefaultPower) obj;
		if (privateFacility == null) {
			if (other.privateFacility != null)
				return false;
		} else if (!privateFacility.equals(other.privateFacility))
			return false;
		if (publicFacility == null) {
			if (other.publicFacility != null)
				return false;
		} else if (!publicFacility.equals(other.publicFacility))
			return false;
		if (semipublicFacility == null) {
			if (other.semipublicFacility != null)
				return false;
		} else if (!semipublicFacility.equals(other.semipublicFacility))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [publicFacility=" + publicFacility + ", semipublicFacility="
				+ semipublicFacility + ", privateFacility=" + privateFacility + "]";
	}

}
