package edu.kit.ifv.mobitopp.data.local;

import edu.kit.ifv.mobitopp.dataimport.ChargingDataFactory;
import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.simulation.emobility.LimitedChargingDataForZone;
import edu.kit.ifv.mobitopp.simulation.emobility.UnlimitedChargingDataForZone;

public enum ChargingType {

	unlimited {
		@Override
		public ChargingDataFactory factory(DefaultPower defaultPower) {
			return facilities -> new UnlimitedChargingDataForZone(facilities, defaultPower);
		}
	}, limited {

		@Override
		public ChargingDataFactory factory(DefaultPower defaultPower) {
			return facilities -> new LimitedChargingDataForZone(facilities, defaultPower);
		}
		
	};

	public abstract ChargingDataFactory factory(DefaultPower defaultPower);
}
