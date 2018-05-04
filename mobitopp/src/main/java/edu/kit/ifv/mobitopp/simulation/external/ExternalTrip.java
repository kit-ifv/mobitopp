package edu.kit.ifv.mobitopp.simulation.external;

import java.lang.Comparable;

public class ExternalTrip 
	implements Comparable<ExternalTrip>
{

	public final int id;

	public final int from;
	public final int to;
	public final int hour;
	public final int minute;

	private static int id_seq = 0;


	public ExternalTrip(
		int from,
		int to,
		int hour,
		int minute
	) {
		this.from = from;
		this.to = to;
		this.hour = hour;
		this.minute = minute;
		this.id = id_seq++; 
	}


	public int compareTo(ExternalTrip o) {

		if (this.hour < o.hour) { 
			return -1; 
		} else if (this.hour > o.hour) {
			return 1; 
		}

		if (this.minute < o.minute) { 
			return -1; 
		} else if (this.minute > o.minute) {
			return 1; 
		}

		if (this.id < o.id) { 
			return -1; 
		} else if (this.id > o.id) {
			return 1; 
		}

		return 0;
	}



	public String toString() {
		return "(from=" + from + ",to=" + to + ",time=" 
				+ String.format("%02d",hour) + ":" + String.format("%02d",minute) + ")";
	}
}
