package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static java.util.Collections.emptyList;

import java.util.List;

import edu.kit.ifv.mobitopp.visum.VisumFace;
import edu.kit.ifv.mobitopp.visum.VisumSurface;

public class VisumSurfaceBuilder {

	private static final int defaultId = 1;
	
	private int id;
	private List<VisumFace> faces;
	private List<Integer> enclave;

	public VisumSurfaceBuilder() {
		super();
		id = defaultId;
		faces = emptyList();
		enclave = emptyList();
	}

	public VisumSurface build() {
		return new VisumSurface(id, faces, enclave);
	}

	public VisumSurfaceBuilder withId(int id) {
		this.id = id;
		return this;
	}

}
