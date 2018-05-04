package edu.kit.ifv.mobitopp.visum;

import java.io.Serializable;

public class VisumTransportSystem implements Serializable {

	private static final long serialVersionUID = 1L;

	public final String code;
	public final String name;
	public final String type;

	public VisumTransportSystem(String code, String name, String type) {
		super();
		this.code = code;
		this.name = name;
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VisumTransportSystem other = (VisumTransportSystem) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VisumTransportSystem [code=" + code + ", name=" + name + ", type=" + type + "]";
	}

}
