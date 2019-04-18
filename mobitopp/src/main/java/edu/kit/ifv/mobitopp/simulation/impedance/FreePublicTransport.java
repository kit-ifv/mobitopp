package edu.kit.ifv.mobitopp.simulation.impedance;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class FreePublicTransport extends ImpedanceDecorator {

	public FreePublicTransport(ImpedanceIfc impedance) {
		super(impedance);
	}
	
	@Override
	public float getTravelCost(ZoneId source, ZoneId target, Mode mode, Time date) {
		
		if(mode == Mode.PUBLICTRANSPORT) {
			return 0.0f;
		}
		
		return super.getTravelCost(source, target, mode, date);
	}

}
