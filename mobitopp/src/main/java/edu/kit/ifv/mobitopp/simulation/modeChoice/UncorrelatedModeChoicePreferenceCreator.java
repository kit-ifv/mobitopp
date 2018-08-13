package edu.kit.ifv.mobitopp.simulation.modeChoice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import edu.kit.ifv.mobitopp.simulation.Mode;

public class UncorrelatedModeChoicePreferenceCreator 
	implements ModeChoicePreferenceCreator {
	
	private final Random random;
	
	private double sd_walking            		      =  0.0; // Reference class
	
	private double	sd_cardriver                     = 1.79411;			//    0.03524   50.92  < 2e-16 ***
	private double	sd_carpassenger                  = 1.90536;			//    0.04115   46.30  < 2e-16 ***
	private double	sd_cycling                       = 2.81035;			//    0.06461   43.50  < 2e-16 ***
	private double	sd_publictransport               = 1.62396;			//    0.04541   35.76  < 2e-16 ***
	
	
	public UncorrelatedModeChoicePreferenceCreator(long seed) {
		this.random = new Random(seed);
	}
	
	
	
	
	public ModeChoicePreferences createPreferences() {
		
		Map<Mode,Double> prefs = new LinkedHashMap<Mode,Double>();
		
		prefs.put(Mode.CAR, 						sd_cardriver*random.nextGaussian());
		prefs.put(Mode.PASSENGER, 			sd_carpassenger*random.nextGaussian());
		prefs.put(Mode.PEDESTRIAN, 			sd_walking*random.nextGaussian());
		prefs.put(Mode.BIKE, 						sd_cycling*random.nextGaussian());
		prefs.put(Mode.PUBLICTRANSPORT, sd_publictransport*random.nextGaussian());
	
		return new ModeChoicePreferences(prefs);
	}

}
