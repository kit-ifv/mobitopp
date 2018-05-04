package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.visum.VisumMatrixParser;

public class VisumMatrixParserTest {

	@Test
	public void parseSmallMatrix() throws IOException {
		CostMatrix matrix = VisumMatrixParser.parse(smallData());
		
		compare(matrix, expectedSmall());
	}

	private CostMatrix expectedSmall() {
		CostMatrix costMatrix = new CostMatrix(asList(1, 2));
		costMatrix.set(1, 1, 1.0f);
		costMatrix.set(1, 2, 2.0f);
		costMatrix.set(2, 1, 3.0f);
		costMatrix.set(2, 2, 4.0f);
		return costMatrix;
	}
	
	@Test
	public void serialiseSmallMatrix() {
		CostMatrix matrix = expectedSmall();
		
		String serialized = VisumMatrixParser.serialize(matrix);
		
		assertEquals(smallData, serialized);
	}
	
	@Test
	public void parseBigMatrix() throws IOException {
		CostMatrix matrix = VisumMatrixParser.parse(bigData());
		
		compare(matrix, expectedBig());
	}

	private CostMatrix expectedBig() {
		List<Integer> zoneIds = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
		CostMatrix costMatrix = new CostMatrix(zoneIds );
		for (Integer source : zoneIds) {
			for (Integer target : zoneIds) {
				costMatrix.set(source, target, 0.0f);
			}
		}
		for (Integer id : zoneIds) {
			costMatrix.set(1, id, id.floatValue());
		}
		for (int i = 1; i < 10; i++) {
			costMatrix.set(1 + i, 1, i * 11.0f);
		}
		return costMatrix;
	}
	
	@Test
	public void serialiseBigMatrix() {
		CostMatrix matrix = expectedBig();
		
		String serialized = VisumMatrixParser.serialize(matrix);
		
		assertEquals(bigData, serialized);
	}

	private void compare(CostMatrix actual, CostMatrix expected) {
		assertThat(actual.type(), is(equalTo(expected.type())));
		assertThat(actual.oids(), is(equalTo(expected.oids())));
		assertThat(actual.oids(), is(equalTo(expected.oids())));
		for (Integer sourceOid : actual.oids()) {
			for (Integer targetOid : actual.oids()) {
				assertThat(actual.get(sourceOid, targetOid),
						is(equalTo(expected.get(sourceOid, targetOid))));
			}
		}
	}
	
	private InputStream smallData() {
		return streamOf(smallData);
	}
	
	private InputStream bigData() {
		return streamOf(bigData);
	}

	private InputStream streamOf(String data) {
		return new ByteArrayInputStream(data.getBytes());
	}

	private static final String smallData = 
			"$V;D3" + System.lineSeparator() + 
			"* Von  Bis" + System.lineSeparator() + 
			"0.00 0.00" + System.lineSeparator() + 
			"* Faktor" + System.lineSeparator() + 
			"1.00" + System.lineSeparator() + 
			"*  " + System.lineSeparator() + 
			"* KIT Karlsruher Institut für Technologie Fakultät Bau, Geo + Umwelt Karlsruhe" + System.lineSeparator() + 
			"* 17.03.17" + System.lineSeparator() + 
			"* Anzahl Netzobjekte" + System.lineSeparator() + 
			"2" + System.lineSeparator() + 
			"* Netzobjekt-Nummern" + System.lineSeparator() + 
			" 1 2"  + System.lineSeparator() + 
			"*" + System.lineSeparator() + 
			"* Obj 1 Summe = 3.0" + System.lineSeparator() + 
			" 1.0 2.0" + System.lineSeparator() + 
			"* Obj 2 Summe = 7.0" + System.lineSeparator() + 
			" 3.0 4.0" + System.lineSeparator() + 
			"* Netzobjektnamen" + System.lineSeparator() + 
			"$NAMES" + System.lineSeparator() + 
			"1 \"1\"" + System.lineSeparator() + 
			"2 \"2\"" + System.lineSeparator() + 
			" ";
	
	private static final String bigData = 
			"$V;D3" + System.lineSeparator() + 
			"* Von  Bis" + System.lineSeparator() + 
			"0.00 0.00" + System.lineSeparator() + 
			"* Faktor" + System.lineSeparator() + 
			"1.00" + System.lineSeparator() + 
			"*  " + System.lineSeparator() + 
			"* KIT Karlsruher Institut für Technologie Fakultät Bau, Geo + Umwelt Karlsruhe" + System.lineSeparator() + 
			"* 17.03.17" + System.lineSeparator() + 
			"* Anzahl Netzobjekte" + System.lineSeparator() + 
			"13" + System.lineSeparator() + 
			"* Netzobjekt-Nummern" + System.lineSeparator() + 
			" 1 2 3 4 5 6 7 8 9 10" + System.lineSeparator() + 
			" 11 12 13" + System.lineSeparator() + 
			"*" + System.lineSeparator() + 
			"* Obj 1 Summe = 91.0" + System.lineSeparator() + 
			" 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 10.0" + System.lineSeparator() + 
			" 11.0 12.0 13.0" + System.lineSeparator() + 
			"* Obj 2 Summe = 11.0" + System.lineSeparator() + 
			" 11.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 3 Summe = 22.0" + System.lineSeparator() + 
			" 22.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 4 Summe = 33.0" + System.lineSeparator() + 
			" 33.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 5 Summe = 44.0" + System.lineSeparator() + 
			" 44.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 6 Summe = 55.0" + System.lineSeparator() + 
			" 55.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 7 Summe = 66.0" + System.lineSeparator() + 
			" 66.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 8 Summe = 77.0" + System.lineSeparator() + 
			" 77.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 9 Summe = 88.0" + System.lineSeparator() + 
			" 88.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 10 Summe = 99.0" + System.lineSeparator() + 
			" 99.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 11 Summe = 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 12 Summe = 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 13 Summe = 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Netzobjektnamen" + System.lineSeparator() + 
			"$NAMES" + System.lineSeparator() + 
			"1 \"1\"" + System.lineSeparator() + 
			"2 \"2\"" + System.lineSeparator() + 
			"3 \"3\"" + System.lineSeparator() + 
			"4 \"4\"" + System.lineSeparator() + 
			"5 \"5\"" + System.lineSeparator() + 
			"6 \"6\"" + System.lineSeparator() + 
			"7 \"7\"" + System.lineSeparator() + 
			"8 \"8\"" + System.lineSeparator() + 
			"9 \"9\"" + System.lineSeparator() + 
			"10 \"10\"" + System.lineSeparator() + 
			"11 \"11\"" + System.lineSeparator() + 
			"12 \"12\"" + System.lineSeparator() + 
			"13 \"13\"" + System.lineSeparator() + 
			" ";
}
