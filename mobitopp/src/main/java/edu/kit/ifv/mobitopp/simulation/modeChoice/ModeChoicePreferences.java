package edu.kit.ifv.mobitopp.simulation.modeChoice;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;

public class ModeChoicePreferences 
	implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	public static final ModeChoicePreferences NOPREFERENCES = createNopreferences();
	
	private static ModeChoicePreferences createNopreferences() {
		Map<Mode,Double> tmp = new LinkedHashMap<Mode,Double>();
		tmp.put(Mode.CAR, 0.0);
		tmp.put(Mode.PASSENGER, 0.0);
		tmp.put(Mode.PEDESTRIAN, 0.0);
		tmp.put(Mode.BIKE, 0.0);
		tmp.put(Mode.PUBLICTRANSPORT, 0.0);
		
		return new ModeChoicePreferences(Collections.unmodifiableMap(tmp));
	}
	
	
	protected final Map<Mode,Double> preferences;

	public  ModeChoicePreferences(Map<Mode,Double> preferences) {
		assert preferences != null;
		assert !preferences.isEmpty();
		
		this.preferences = Collections.unmodifiableMap(new LinkedHashMap<Mode,Double>(preferences));
	}
	
	public Map<Mode,Double> asMap() {
		
		return Collections.unmodifiableMap(preferences);
	}
	
	public String asCommaSeparatedString( ) {
		
		return preferences.entrySet().stream().map(x -> "" + x.getKey() + "=" + x.getValue() ).collect(Collectors.joining(","));
	}

	@Override
	public String toString() {
		return asCommaSeparatedString();
	}
	
	public static ModeChoicePreferences fromCommaSeparatedString(String input) {
	
			List<String> entries = Arrays.asList(input.split(","));
			
			Map<Mode,Double> values = new LinkedHashMap<Mode,Double>();
			
			for(String entry : entries) {
				String parts[] = entry.split("=");
				Mode key = StandardMode.valueOf(parts[0].trim());
				Double value = Double.valueOf(parts[1].trim());
				
				values.put(key, value);
			}
			
			return new ModeChoicePreferences(values);
	}

	public double get(Mode mode) {
		return preferences.get(mode);
	}
}
