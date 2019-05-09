package edu.kit.ifv.mobitopp.util.dataexport;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.Matrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;

public class MatrixPrinter {

	private static final int defaultValuesPerLine = 10;
  private final Map<Integer,String> names;
  private final int valuesPerLine;

	public MatrixPrinter(Map<Integer,String> names, int valuesPerLine) {
		super();
		this.names = names;
    this.valuesPerLine = valuesPerLine;
	}
	
	public MatrixPrinter(Map<Integer, String> names) {
	  this(names, defaultValuesPerLine);
	}

	public void writeMatrixToFile(
		Matrix<? extends Number> matrix,
		String from,
		String to,
		String filename
	) {

		File outputFile = new File(filename);
		writeMatrixToFile(matrix, from, to, outputFile);
	}

	public void writeMatrixToFile(
			Matrix<? extends Number> matrix, String from, String to, File toOutputFile) {
		String data = dataToString(matrix);
		String header = header(matrix, from, to);

		createParentFoldersOf(toOutputFile);
		String content = header + data;
		write(content, toOutputFile);
	}

	private void write(String content, File outputFile) {
		try (FileWriter fw = new FileWriter(outputFile)) {
			System.out.println("writing matrix file: " + outputFile.getAbsolutePath());
			fw.write(content);
			System.out.println("DONE.");
		} catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}
	}

	private void createParentFoldersOf(File outputFile) {
		outputFile.getParentFile().mkdirs();
	}

	private String header(
		Matrix<? extends Number> matrix, 
		String from,
		String to
	) {

		String typ = "$V"; // Add number of decimals

		String factor = "1.00";
		String names = names(matrix); 
		String number = Integer.toString(matrix.ids().size()); 


		StringBuffer buf = new StringBuffer();

		buf.append(typ);
		buf.append("\n* Von Bis\n");
		buf.append(from + " " + to);
		buf.append("\n* Faktor\n");
		buf.append(factor);
		buf.append("\n* Anzahl Netzobjekte\n");
		buf.append(number);
		buf.append("\n* Netzobjekt-Nummern\n");
		buf.append(names);
		buf.append("\r\n");
		buf.append("*\n");
			

		return buf.toString();
	}


	private String names(Matrix<? extends Number> matrix) {

		List<ZoneId> matrixOids = new ArrayList<>(matrix.ids());

		String matrixIds = "";

		int cnt = 0;

		for (ZoneId id : matrixOids) {
      matrixIds += String
          .format("%1$10s",
              (!this.names.isEmpty() ? this.names.get(id.getMatrixColumn()) : id.getExternalId()))
          + " ";
   		matrixIds += (++cnt % 10 == 0 ? "\r\n" : ""); 
 		}

		return matrixIds;
	}

	public static MatrixPrinter fromZones(Map<ZoneId, Zone> zones) {
		return new MatrixPrinter(makeNames(zones));
	}
	
	private static Map<Integer,String> makeNames(Map<ZoneId, Zone> zones) {
		Map<Integer,String> names = new HashMap<>();
		for (ZoneId id : zones.keySet()) {
			String zoneId = id.getExternalId().replace("Z","");
			names.put(id.getMatrixColumn(), zoneId);
		}
		return names;
	}

	private String dataToString(Matrix<? extends Number> matrix) {

		Collection<ZoneId> ids = matrix.ids();

		StringBuffer buf = new StringBuffer(2800000);

		for(ZoneId row : ids) {

			StringBuffer buf_line = new StringBuffer(28000);

			float total = 0.0f;

			int cnt = 0;

			for(ZoneId col : ids) {

				Number val = matrix.get(row,col);
				buf_line.append( String.format(Locale.US, "%1$6.3f",val.doubleValue()) ).append(" ");

				total += val.floatValue();

        if(++cnt % valuesPerLine == 0) { buf_line.append("\r\n"); }
			}
			buf.append("* Obj " + row.getExternalId() + " Summe = " + String.format(Locale.US, "%1$6.3f",total) + "\r\n");
			buf.append(buf_line.toString());
			buf.append("\r\n");
		}

		return buf.toString();
	}

}
