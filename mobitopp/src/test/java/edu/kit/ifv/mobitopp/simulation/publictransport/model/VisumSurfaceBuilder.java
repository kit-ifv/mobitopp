package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import java.util.LinkedList;
import java.util.List;

import edu.kit.ifv.mobitopp.visum.VisumFace;
import edu.kit.ifv.mobitopp.visum.VisumSurface;

public class VisumSurfaceBuilder {

	private static final int defaultId = 1;
	
	private int id;
	private final List<VisumFace> faces;
	private final List<Integer> enclaves;

	public VisumSurfaceBuilder() {
		super();
		id = defaultId;
		faces = new LinkedList<>();
		enclaves = new LinkedList<>();
	}

	public VisumSurface build() {
		return new VisumSurface(id, faces, enclaves);
	}

	public VisumSurfaceBuilder withId(int id) {
		this.id = id;
		return this;
	}
	
	public VisumSurfaceBuilder withFace(VisumFace face, int enclave) {
	  faces.add(face);
	  enclaves.add(enclave);
	  return this;
	}
	
}
