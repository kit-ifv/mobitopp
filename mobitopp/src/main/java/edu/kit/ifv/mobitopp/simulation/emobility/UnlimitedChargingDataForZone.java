package edu.kit.ifv.mobitopp.simulation.emobility;

import java.util.List;

import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility.Type;

public class UnlimitedChargingDataForZone extends BaseChargingDataForZone {

	private static final long serialVersionUID = 1L;

	static final int unlimitedPoints = Integer.MAX_VALUE;
	static final int unlimitedId = -3;

	public UnlimitedChargingDataForZone(
			List<ChargingFacility> chargingFacilities, DefaultPower defaultPower) {
		super(chargingFacilities, defaultPower);
	}

	public UnlimitedChargingDataForZone(
			List<ChargingFacility> chargingFacilities, double privateChargingProbabilty,
			DefaultPower defaultPower) {
		super(chargingFacilities, privateChargingProbabilty, defaultPower);
	}

	@Override
	public int numberOfAvailableChargingPoints(List<ChargingFacility> chargingFacilities) {
		return unlimitedPoints;
	}

	@Override
	protected ChargingFacility freshChargingPoint(Location location, DefaultPower defaultPower) {
		return new ChargingFacility(unlimitedId, location, Type.PUBLIC, defaultPower.publicFacility());
	}
}
