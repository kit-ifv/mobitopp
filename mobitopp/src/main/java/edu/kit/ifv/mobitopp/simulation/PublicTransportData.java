package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.result.ResultWriter;

public interface PublicTransportData {

	/**
	 * Use {@link #empty()} instead.
	 */
	PublicTransportData empty = new PublicTransportData() {

		@Override
		public ImpedanceIfc impedance(ImpedanceIfc impedance, ZoneRepository zoneRepository) {
			return impedance;
		}

		@Override
		public VehicleBehaviour vehicleBehaviour(ResultWriter results) {
			return VehicleBehaviour.empty;
		}
	};

	static PublicTransportData empty() {
		return empty;
	}

	VehicleBehaviour vehicleBehaviour(ResultWriter results);

	ImpedanceIfc impedance(ImpedanceIfc impedance, ZoneRepository zoneRepository);

}
