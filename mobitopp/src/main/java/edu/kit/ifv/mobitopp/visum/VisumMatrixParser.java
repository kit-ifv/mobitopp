package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.MatrixParser;
import edu.kit.ifv.mobitopp.util.file.StreamContent;

public class VisumMatrixParser implements MatrixParser {

  private final IdToOidMapper idToOidMapper;
	private final BufferedReader reader;
	private final List<String> zonesIds;
	private final List<Float> data;
	private String currentLine;
	private int readZones;

	private VisumMatrixParser(InputStream input, IdToOidMapper idToOidMapper) {
		super();
    this.idToOidMapper = idToOidMapper;
		reader = new BufferedReader(new InputStreamReader(input));
		zonesIds = new ArrayList<>();
		data = new ArrayList<>();
		currentLine = "";
		readZones = 0;
	}
	
	private static InputStream asStream(File inputFile) throws IOException {
		return StreamContent.of(inputFile);
	}

	public static CostMatrix parse(InputStream inputStream, IdToOidMapper idToOidMapper) throws IOException {
		return new VisumMatrixParser(inputStream, idToOidMapper).parseCostMatrix();
	}

	public static VisumMatrixParser load(File file, IdToOidMapper idToOidMapper) throws IOException {
		return new VisumMatrixParser(asStream(file), idToOidMapper);
	}

	public static String serialize(FloatMatrix matrix, OidToIdMapper mapper) {
		return header() + zones(matrix, mapper) + values(matrix) + zoneNames(matrix, mapper);
	}

	private static String zoneNames(FloatMatrix matrix, OidToIdMapper mapper) {
		StringBuilder names = new StringBuilder();
		names.append("* Netzobjektnamen");
		names.append(System.lineSeparator()); 
		names.append("$NAMES"); 
		names.append(System.lineSeparator());
		for (int oid : matrix.oids()) {
			String zoneId = mapper.map(oid);
      names.append(zoneId);
			names.append(" \"");
			names.append(oid);
			names.append("\""); 
			names.append(System.lineSeparator()); 
		}
		names.append(" ");;
		return names.toString();
	}

	private static String values(FloatMatrix matrix) {
		StringBuilder values = new StringBuilder();
		for (int zone : matrix.oids()) {
			values.append("* Obj ");
			values.append(zone);
			values.append(" Summe = ");
			values.append(rowSumOf(zone, matrix));
			values.append(System.lineSeparator());
			values.append(columnValuesOf(zone, matrix));
			values.append(System.lineSeparator());
		}
		return values.toString();
	}

	private static String columnValuesOf(int zone, FloatMatrix matrix) {
		StringBuilder values = new StringBuilder();
		int serializedZones = 0;
		for (int column : matrix.oids()) {
			if (0 == serializedZones % 10 && 0 != serializedZones) {
				values.append(System.lineSeparator());
			}
			values.append(" ");
			values.append(matrix.get(zone, column));
			serializedZones++;
		}
		return values.toString();
	}

	private static String rowSumOf(int zone, FloatMatrix matrix) {
		double sum = 0.0;
		for (int column : matrix.oids()) {
			sum += matrix.get(zone, column);
		}
		return String.valueOf(sum);
	}

	private static String zones(FloatMatrix matrix, OidToIdMapper mapper) {
		List<Integer> oids = matrix.oids();
		StringBuilder serializedOids = new StringBuilder();
		int serializedZones = 0;
		for (int oid : oids) {
			if (0 == serializedZones % 10 && 0 != serializedZones) {
				serializedOids.append(System.lineSeparator());
			}
			serializedOids.append(" ");
			String zoneId = mapper.map(oid);
      serializedOids.append(zoneId);
			serializedZones++;
		}
		serializedOids.append(System.lineSeparator());
		return "* Anzahl Netzobjekte" + System.lineSeparator() + 
				oids.size() + System.lineSeparator() + 
				"* Netzobjekt-Nummern" + System.lineSeparator() + 
				serializedOids.toString() + 
				"*" + System.lineSeparator();
	}

	private static String header() {
		return "$V;D3" + System.lineSeparator() + 
				"* Von  Bis" + System.lineSeparator() + 
				"0.00 0.00" + System.lineSeparator() + 
				"* Faktor" + System.lineSeparator() + 
				"1.00" + System.lineSeparator() + 
				"*  " + System.lineSeparator() + 
				"* KIT Karlsruher Institut für Technologie Fakultät Bau, Geo + Umwelt Karlsruhe" + System.lineSeparator() + 
				"* 17.03.17" + System.lineSeparator();
	}
	
	@Override
	public FloatMatrix parseMatrix() throws IOException {
		return parse(FloatMatrix::new);
	}
	
	@Override
	public CostMatrix parseCostMatrix() throws IOException {
		return parse(CostMatrix::new);
	}
	
	@Override
	public FixedDistributionMatrix parseFixedDistributionMatrix() throws IOException {
		return parse(FixedDistributionMatrix::new);
	}
	
	@Override
	public TravelTimeMatrix parseTravelTimeMatrix() throws IOException {
		return parse(TravelTimeMatrix::new);
	}

	private <T extends FloatMatrix> T parse(Function<List<Integer>, T> creator)
			throws IOException {
		skipHeader();
		readZones();
		readCellValues();
		return build(creator);
	}

	private void skipHeader() throws IOException {
		while (isReady() && !startOfCellSection()) {
			readLine();
		}
	}

	private void readZones() throws IOException {
		if (startOfCellSection()) {
			readLine();
			int numberOfZones = Integer.valueOf(currentLine);
			readLine();
			while (readZones < numberOfZones) {
				readLine();
				parseCurrentZones();
			}
		}
	}

	private <T extends FloatMatrix> T build(Function<List<Integer>, T> creator) {
		T matrix = createMatrix(creator);
		for (int index = 0; index < data.size(); index++) {
			int size = zonesIds.size();
			int row = index / size + 1;
			int column = index % size + 1;
			float value = data.get(index);
			matrix.set(row, column, value);
		}
		return matrix;
	}

  private <T extends FloatMatrix> T createMatrix(Function<List<Integer>, T> creator) {
    List<Integer> oids = zonesIds.stream().map(idToOidMapper::map).collect(toList());
    return creator.apply(oids);
  }

	private boolean isReady() throws IOException {
		return reader.ready();
	}

	private boolean startOfCellSection() {
		return currentLine.equals("* Anzahl Netzobjekte");
	}

	private void readCellValues() throws IOException {
		while (isReady() && isNotFooter()) {
			readLine();
			if (lineContainsData()) {
				parseCurrentValues();
			}
		}
	}

	private void parseCurrentValues() {
		for (String token : parseLine()) {
			data.add(valueOf(token));
		}
	}

	private float valueOf(String token) {
		return Float.parseFloat(token);
	}

	private boolean lineContainsData() {
		return !currentLine.startsWith("*") && !currentLine.isEmpty();
	}

	private boolean isNotFooter() {
		return !currentLine.startsWith("* Netzobjektnamen");
	}

	private void readLine() throws IOException {
		currentLine = reader.readLine();
	}

	private void parseCurrentZones() {
		for (String token : parseLine()) {
			zonesIds.add(token);
			readZones++;
		}
	}

	private List<String> parseLine() {
		String[] tokens = currentLine.trim().split("\\s+");
		return asList(tokens);
	}

}
