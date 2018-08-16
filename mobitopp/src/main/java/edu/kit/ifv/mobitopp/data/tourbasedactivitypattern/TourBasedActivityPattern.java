package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.ArrayList;
import java.util.List;

public class TourBasedActivityPattern {
	
	private List<TourBasedActivityPatternElement>  elements;
	
	public TourBasedActivityPattern(List<TourBasedActivityPatternElement> elements) {
		this.elements  = new ArrayList<TourBasedActivityPatternElement>(elements);
	}

}
