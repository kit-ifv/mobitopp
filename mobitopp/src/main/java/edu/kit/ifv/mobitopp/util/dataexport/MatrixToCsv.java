package edu.kit.ifv.mobitopp.util.dataexport;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.visum.VisumMatrixParser;

public class MatrixToCsv {

	private static final String matrixFileExtension = ".mtx";
	private final FloatMatrix matrix;

	public MatrixToCsv(FloatMatrix matrix) {
		super();
		this.matrix = matrix;
	}

	public String toCsv() {
		CsvBuilder csv = new CsvBuilder();
		csv.append("Von");
		csv.append("Nach");
		csv.newLine("Zeit");
		for (int origin = 1; origin <= size(); origin++) {
			for (int destination = 1; destination <= size(); destination++) {
				float traveltime = matrix.get(origin, destination);
				csv.append(zoneIdFor(origin));
				csv.append(zoneIdFor(destination));
				csv.newLine(traveltime);
			}
		}
		return csv.toString();
	}

	private int zoneIdFor(int index) {
		return oids().get(index - 1);
	}

	private int size() {
		return oids().size();
	}

	private List<Integer> oids() {
		return matrix.oids();
	}

	public static void main(String[] args) throws IOException {
		if (1 != args.length) {
			System.out.println("Usage: ... <visum matrix folder>");
			System.exit(1);
		}

		File basePath = new File(args[0]);
		File[] listFiles = basePath.listFiles((dir, name) -> name.endsWith(matrixFileExtension));
		if (null != listFiles && 0 < listFiles.length) {
			for (File matrix : listFiles) {
				convertMatrix(matrix);
			}
			return;
		}
		convert(basePath);
	}

	private static void convert(File basePath) {
		File matrixFolder = new File(basePath, "traveltime");
		for (File matrix : matrixFolder.listFiles((dir, name) -> name.endsWith(matrixFileExtension))) {
			convertMatrix(matrix);
		}
	}

	private static void convertMatrix(File input) {
		try {
			TravelTimeMatrix travelTimeMatrix = VisumMatrixParser.load(input).parseTravelTimeMatrix();
			String csv = new MatrixToCsv(travelTimeMatrix).toCsv();

			try (Writer writer = Files.newBufferedWriter(outputFor(input), Charset.defaultCharset())) {
				writer.write(csv);
			}
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
	}

	private static Path outputFor(File input) {
		String derivedName = input.getName().replaceAll("\\.mtx", ".csv");
		return new File(input.getParentFile(), derivedName).toPath();
	}
}
