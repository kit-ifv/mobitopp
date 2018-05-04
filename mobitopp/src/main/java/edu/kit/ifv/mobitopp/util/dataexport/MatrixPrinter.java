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

public class MatrixPrinter {

	private final Map<Integer,String> names;

	MatrixPrinter(Map<Integer,String> names) {
		super();
		this.names = names;
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
		String number = Integer.toString(matrix.oids().size()); 


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

		List<Integer> matrixOids = new ArrayList<Integer>(matrix.oids());

		String matrixIds = "";

		int cnt = 0;

		for (Integer oid : matrixOids) {
   		matrixIds += String.format("%1$10s",(!this.names.isEmpty() ? this.names.get(oid) : oid)) + " ";
   		matrixIds += (++cnt % 10 == 0 ? "\r\n" : ""); 
 		}

		return matrixIds;
	}

	public static MatrixPrinter fromZones(Map<Integer,Zone> zones) {
		return new MatrixPrinter(makeNames(zones));
	}
	
	private static Map<Integer,String> makeNames(Map<Integer,Zone> zones) {

		Map<Integer,String> names = new HashMap<Integer,String>();

		for (Integer id : zones.keySet()) {

			Zone zone = zones.get(id);

   		String zoneId = zone.getId().replace("Z","");

			names.put(id, zoneId);
		}

		return names;
	}


	private static String dataToString(Matrix<? extends Number> matrix) {

		Collection<Integer> oids = matrix.oids();

		StringBuffer buf = new StringBuffer(2800000);

		for(Integer row : oids) {

			StringBuffer buf_line = new StringBuffer(28000);

			float total = 0.0f;

			int cnt = 0;

			for(Integer col : oids) {

				Number val = matrix.get(row,col);
				buf_line.append( String.format(Locale.US, "%1$6.3f",val.doubleValue()) ).append(" ");

				total += val.floatValue();

   			if(++cnt % 10 == 0) { buf_line.append("\r\n"); }
			}
			buf.append("* Obj " + row + " Summe = " + String.format(Locale.US, "%1$6.3f",total) + "\r\n");
			buf.append(buf_line.toString());
			buf.append("\r\n");
		}

		return buf.toString();
	}

}
