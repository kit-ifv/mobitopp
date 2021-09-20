package edu.kit.ifv.mobitopp.visum.exportconfiguration;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.TableDescription;

public class ReportingTableDescription extends TableDescription {

	private final ReportingRow firstRow;
	private final ReportingRow secondRow;

	public ReportingTableDescription(String attributeSeparator, Map<String, String> defaultValues) {
		super(0, 1, Collections.emptyList(), attributeSeparator);
		firstRow = new ReportingRow(defaultValues, 0);
		secondRow = new ReportingRow(defaultValues, 1);
	}

	@Override
	public Stream<Row> convert(Stream<String> lines) {
		return Stream.of(firstRow, secondRow);
	}

	public String serialise() {
		return firstRow.serialise();
	}

}
