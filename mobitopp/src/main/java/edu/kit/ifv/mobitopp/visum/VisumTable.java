package edu.kit.ifv.mobitopp.visum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class VisumTable {

	public final String name;
	public final List<String> attributes;
	private final List<Map<String,String>> rows;

	public VisumTable(String name, List<String> attributes) {
		this.name=name;
		this.attributes=Collections.unmodifiableList(attributes);
		rows = new ArrayList<>();
	}

	public void addRow(List<String> values) 
		throws IllegalArgumentException 
	{

		if (values.size() != attributes.size()) {

			System.out.println("Attributes: " + attributes);
			System.out.println("Values: " + values);

			throw new IllegalArgumentException();
		}

		Map<String,String> row = new HashMap<String,String> ();

		for (int i=0; i<attributes.size(); i++) {

			row.put(attributes.get(i), values.get(i));
		}
		rows.add(Collections.unmodifiableMap(row));
	}

	public int numberOfRows() {
		return rows.size();
	}

	public Row getRow(int row) {
		return new Row(rows.get(row));
	}

	public String getValue(int row, String attribute) {

		assert containsAttribute(attribute) : ("table <" + name + "> does not contain attribute <" + attribute + ">"); ;

		return rows.get(row).get(attribute);
	}

	public boolean containsAttribute(String attrib) {
		return attributes.contains(attrib);
	}
	
	public Stream<Row> rows() {
	  return rows.stream().map(Row::new);
	}
}
