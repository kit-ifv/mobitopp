package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.File;

public class StoredMatrix {

	private String path;
	private TimeSpan timeSpan;

	public StoredMatrix() {
		super();
	}
	
	public StoredMatrix(String path) {
		super();
		this.path = path;
	}

	public StoredMatrix(TimeSpan timeSpan, String path) {
		this.timeSpan = timeSpan;
		this.path = path;
	}
	
	public TimeSpan timeSpan() {
		return timeSpan;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public File file(File baseFolder) {
		return new File(baseFolder, path);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((timeSpan == null) ? 0 : timeSpan.hashCode());
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
		StoredMatrix other = (StoredMatrix) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (timeSpan == null) {
			if (other.timeSpan != null)
				return false;
		} else if (!timeSpan.equals(other.timeSpan))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StoredMatrix [path=" + path + ", timeSpan=" + timeSpan + "]";
	}

}
