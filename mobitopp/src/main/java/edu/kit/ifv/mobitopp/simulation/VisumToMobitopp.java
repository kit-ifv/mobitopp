package edu.kit.ifv.mobitopp.simulation;

public class VisumToMobitopp {

	private String carTransportSystemCode = "P";

	public VisumToMobitopp() {
		super();
	}

	public String getCarTransportSystemCode() {
		return carTransportSystemCode;
	}

	public void setCarTransportSystemCode(String carTransportSystemCode) {
		this.carTransportSystemCode = carTransportSystemCode;
	}

	@Override
	public String toString() {
		return "VisumToMobitopp [carTransportSystemCode=" + carTransportSystemCode + "]";
	}

}
