package edu.kit.ifv.mobitopp.simulation.modeChoice;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.util.collections.StreamUtils;

public class ModeChoicePreferences 
	implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	public static final ModeChoicePreferences NOPREFERENCES = createAllSamePreferences(0.0d);

	public static final ModeChoicePreferences ALL_SAME = createAllSamePreferences(1.0d);
	
	private static ModeChoicePreferences createAllSamePreferences(double preference) {
    return new ModeChoicePreferences(Collections
        .unmodifiableMap(EnumSet
            .allOf(StandardMode.class)
            .stream()
            .collect(StreamUtils.toLinkedMap(Function.identity(), mode -> preference))));
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
