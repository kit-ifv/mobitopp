package edu.kit.ifv.mobitopp.visum;

import java.util.Map;
import java.io.Serializable;
import java.util.Collections;

@SuppressWarnings("serial")
public class VisumPtTimeProfile 
	implements Serializable
{


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
