package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VisumPtTransferWalkTimes 
	implements Serializable
{

	public final VisumPtStopArea fromArea;
	public final VisumPtStopArea toArea;
	public final String vsysCode;
	public final Integer time;

	public VisumPtTransferWalkTimes(VisumPtStopArea fromArea, VisumPtStopArea toArea, String vsysCode,
			Integer time) {
		super();
		this.fromArea = fromArea;
		this.toArea = toArea;
		this.vsysCode = vsysCode;
		this.time = time;
	}

	@Override
	public String toString() {
		return "VisumPtTransferWalkTimes [fromArea=" + fromArea + ", toArea=" + toArea + ", vsysCode="
				+ vsysCode + ", time=" + time + "]";
	}

}
