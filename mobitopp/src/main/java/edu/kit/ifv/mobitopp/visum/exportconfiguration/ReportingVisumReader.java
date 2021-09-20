package edu.kit.ifv.mobitopp.visum.exportconfiguration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.visum.TableDescription;
import edu.kit.ifv.mobitopp.visum.reader.VisumFileReader;


public class ReportingVisumReader extends VisumFileReader {
	
	private final Map<String, Map<String, String>> defaultValuesPerTable;
	private final LinkedHashMap<String, ReportingTableDescription> requestedTables;

	public ReportingVisumReader(Map<String, Map<String, String>> defaultValuesPerTable) {
		super();
		this.defaultValuesPerTable = defaultValuesPerTable;
		this.requestedTables = new LinkedHashMap<>();
	}

	public ReportingVisumReader(String attributeSeparator, Charset charset, Map<String, Map<String, String>> defaultValuesPerTable) {
		super(attributeSeparator, charset);
		this.defaultValuesPerTable = defaultValuesPerTable;
		this.requestedTables = new LinkedHashMap<>();
	}

	@Override
	protected TableDescription getTableDescription(File file, Charset charset, String tableName)
		throws IOException {
		ReportingTableDescription description = new ReportingTableDescription(tableName, getDefaultValuesFor(tableName));
		requestedTables.put(tableName, description);
		return description;
	}

	private Map<String, String> getDefaultValuesFor(String tableName) {
		return defaultValuesPerTable.getOrDefault(tableName, Collections.emptyMap());
	}

	@Override
	protected BufferedReader createReader(File routesFile, Charset charset) throws IOException {
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(new byte[0])));
	}

	public void serialiseExportFile(Consumer<String> output) {
		requestedTables.entrySet().stream().map(this::serialise).forEach(output);
	}
	
	private String serialise(Entry<String, ReportingTableDescription> entry) {
		StringBuilder builder = new StringBuilder();
		builder.append("$");
		builder.append(entry.getKey());
		builder.append(":");
		builder.append(entry.getValue().serialise());
		return builder.toString();
	}
	
//	* 
//	* Tabelle: POI-Kategorien
//	* 
//	$POICATEGORY:NO;CODE;NAME;COMMENT;PARENTCATNO

}
