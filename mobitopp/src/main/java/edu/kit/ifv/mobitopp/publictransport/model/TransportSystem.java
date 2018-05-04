package edu.kit.ifv.mobitopp.publictransport.model;

public class TransportSystem {

	private final String code;

	public TransportSystem(String code) {
		super();
		this.code = code;
	}

	public String code() {
		return code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		TransportSystem other = (TransportSystem) obj;
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TransportSystem [code=" + code + "]";
	}

}
