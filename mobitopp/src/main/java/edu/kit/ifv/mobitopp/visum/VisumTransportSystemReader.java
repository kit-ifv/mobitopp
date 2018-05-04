package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisumTransportSystemReader {

	private static final String code = "CODE";
	private static final String name = "NAME";
	private static final String type = "TYP";

	private final VisumTable table;
	private final Map<String, VisumTransportSystem> readSystems;

	public VisumTransportSystemReader(VisumTable table) {
		super();
		this.table = table;
		readSystems = new HashMap<>();
	}

	public VisumTransportSystems readTransportSystems() {
		for (int row = 0; row < table.numberOfRows(); row++) {
			createSystemIn(row);
		}
		return new VisumTransportSystems(readSystems);
	}

	private void createSystemIn(int row) {
		verifySystemIn(row);
		readSystems.put(codeIn(row), systemIn(row));
	}

	private void verifySystemIn(int row) {
		if (readSystems.containsKey(codeIn(row))) {
			throw new IllegalArgumentException(
					"VisumTransportSystem: transport system with code=" + codeIn(row) + " already exists");
		}
	}

	private VisumTransportSystem systemIn(int row) {
		return new VisumTransportSystem(codeIn(row), nameIn(row), typeIn(row));
	}

	private String codeIn(int row) {
		return table.getValue(row, code);
	}

	private String nameIn(int row) {
		return table.getValue(row, name);
	}

	private String typeIn(int i) {
		return table.getValue(i, type);
	}

	public static List<String> attributes() {
		return asList(code, name, type);
	}

}
