package edu.kit.ifv.mobitopp.result;

import java.util.Collections;
import java.util.List;

public class Category {

	private final List<String> columns;
	private final String name;

	Category(String name) {
		super();
		this.name = name;
		columns = Collections.emptyList();
	}

	public Category(String name, List<String> columns) {
		super();
		this.name = name;
		this.columns = columns;
	}

	public String header() {
		CsvBuilder builder = new CsvBuilder();
		columns.stream().forEach(builder::append);
		return builder.toString();
	}

	public String name() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Category other = (Category) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [columns=" + columns + ", name=" + name + "]";
	}

}
