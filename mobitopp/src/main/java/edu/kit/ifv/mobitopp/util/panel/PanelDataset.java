package edu.kit.ifv.mobitopp.util.panel;

import java.util.Objects;

public class PanelDataset {
	
	public final int datasetId;
	public final short year;

	public PanelDataset(int datasetId, short year) {
		this.datasetId = datasetId;
		this.year = year;
	}
	
	public PanelDataset(int datasetId, int year) {
		this(datasetId, (short) year);
	}

	public String toString() {
		return "(" + datasetId + ": " + year + ")";
	}

	@Override
	public boolean equals(Object other) {

		if (!(other instanceof PanelDataset)) return false;

		PanelDataset o = (PanelDataset) other;

		return datasetId==o.datasetId && year==o.year;
	}

	@Override
	public int hashCode() {
		return Objects.hash(datasetId, year);
	}

}
