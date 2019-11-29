package edu.kit.ifv.mobitopp.simulation.impedance;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.Time;

public class ImprovedPublicTransport extends ImpedanceDecorator {
	
	private final float travelTimeFactor;

	public ImprovedPublicTransport(ImpedanceIfc impedance, float travelTimeFactor) {
		super(impedance);
		this.travelTimeFactor = travelTimeFactor;
	}
	
	
	@Override
	public float getTravelTime(ZoneId source, ZoneId target, Mode mode, Time date) {
		
		if(mode == StandardMode.PUBLICTRANSPORT) {
			return travelTimeFactor * super.getTravelTime(source, target, mode, date);
		}
		
		return super.getTravelTime(source, target, mode, date);
	}
	

}
