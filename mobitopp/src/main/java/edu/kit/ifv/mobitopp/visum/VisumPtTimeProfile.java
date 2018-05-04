package edu.kit.ifv.mobitopp.visum;

import java.util.Map;
import java.util.Collections;

public class VisumPtTimeProfile {


	public String name;
	public VisumPtLineRoute route;

	public Map<Integer,VisumPtTimeProfileElement> elements;


	public VisumPtTimeProfile(
		String name,
		VisumPtLineRoute route,
		Map<Integer,VisumPtTimeProfileElement> elements
	) {

			this.name = name;
			this.route = route;
			this.elements = Collections.unmodifiableMap(elements);
	}

}
