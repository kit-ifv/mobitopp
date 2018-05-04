package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.io.BufferedWriter;
import java.io.IOException;

public class Transfer {

	private final String zone;
	private final String singleStop;
	private final String time;

	public Transfer(String zone, String singleStop, String time) {
		super();
		this.zone = zone;
		this.singleStop = singleStop;
		this.time = time;
	}

	public void writeTo(BufferedWriter toOutput) throws IOException {
		write(toOutput);
	}

	private void write(BufferedWriter output) throws IOException {
		output.write(zone + "," + singleStop + "," + time);
		output.newLine();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((singleStop == null) ? 0 : singleStop.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Transfer other = (Transfer) obj;
		if (singleStop == null) {
			if (other.singleStop != null) {
				return false;
			}
		} else if (!singleStop.equals(other.singleStop)) {
			return false;
		}
		if (time == null) {
			if (other.time != null) {
				return false;
			}
		} else if (!time.equals(other.time)) {
			return false;
		}
		if (zone == null) {
			if (other.zone != null) {
				return false;
			}
		} else if (!zone.equals(other.zone)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Transfer [zone=" + zone + ", singleStop=" + singleStop + ", time=" + time + "]";
	}

}
