package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.List;

import edu.kit.ifv.mobitopp.data.PatternActivity;

class PartsAndSubtoursOfMainActivity {
	
	public final List<PatternActivity> parts;
	public final List<List<PatternActivity>> subtours;
	
	public PartsAndSubtoursOfMainActivity(List<PatternActivity> parts, List<List<PatternActivity>> subtours ) {
		this.parts = parts;
		this.subtours = subtours;
	}
}
