package edu.kit.ifv.mobitopp.simulation;

import java.io.Serializable;

public class IdSequence implements Serializable {

	private static final long serialVersionUID = 1L;

	private int counter = 0;

	public int nextId() {
		return ++counter;
	}

}
