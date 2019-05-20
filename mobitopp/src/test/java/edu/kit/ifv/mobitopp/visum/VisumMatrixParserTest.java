package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.ZoneId;

public class VisumMatrixParserTest {

  @Test
	public void parseSmallMatrix() throws IOException {
		CostMatrix matrix = VisumMatrixParser.parse(smallData());
		
		compare(matrix, expectedSmall());
	}

  private CostMatrix expectedSmall() {
		CostMatrix costMatrix = new CostMatrix(asList(newZoneId(0), newZoneId(1)));
		costMatrix.set(0, 0, 1.0f);
		costMatrix.set(0, 1, 2.0f);
		costMatrix.set(1, 0, 3.0f);
		costMatrix.set(1, 1, 4.0f);
		return costMatrix;
	}

  private ZoneId newZoneId(int matrixColumn) {
    int externalId = matrixColumn + 10;
    return new ZoneId("" + externalId , matrixColumn);
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
    List<ZoneId> zoneIds = IntStream.rangeClosed(0, 12).mapToObj(this::newZoneId).collect(toList());
    CostMatrix costMatrix = new CostMatrix(zoneIds);
		for (ZoneId source : zoneIds) {
			for (ZoneId target : zoneIds) {
				costMatrix.set(source, target, 0.0f);
			}
		}
		for (ZoneId id : zoneIds) {
			costMatrix.set(newZoneId(0), id, (float) id.getMatrixColumn() + 1);
		}
		for (int i = 1; i < 10; i++) {
			costMatrix.set(newZoneId(i), newZoneId(0), i * 11.0f);
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
		assertThat(actual.ids(), is(equalTo(expected.ids())));
		assertThat(actual.ids(), is(equalTo(expected.ids())));
		for (ZoneId sourceOid : actual.ids()) {
			for (ZoneId targetOid : actual.ids()) {
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
			" 10 11"  + System.lineSeparator() + 
			"*" + System.lineSeparator() + 
			"* Obj 0 Summe = 3.0" + System.lineSeparator() + 
			" 1.0 2.0" + System.lineSeparator() + 
			"* Obj 1 Summe = 7.0" + System.lineSeparator() + 
			" 3.0 4.0" + System.lineSeparator() + 
			"* Netzobjektnamen" + System.lineSeparator() + 
			"$NAMES" + System.lineSeparator() + 
			"10 \"0\"" + System.lineSeparator() + 
			"11 \"1\"" + System.lineSeparator() + 
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
			" 10 11 12 13 14 15 16 17 18 19" + System.lineSeparator() + 
			" 20 21 22" + System.lineSeparator() + 
			"*" + System.lineSeparator() + 
			"* Obj 0 Summe = 91.0" + System.lineSeparator() + 
			" 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 10.0" + System.lineSeparator() + 
			" 11.0 12.0 13.0" + System.lineSeparator() + 
			"* Obj 1 Summe = 11.0" + System.lineSeparator() + 
			" 11.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 2 Summe = 22.0" + System.lineSeparator() + 
			" 22.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 3 Summe = 33.0" + System.lineSeparator() + 
			" 33.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 4 Summe = 44.0" + System.lineSeparator() + 
			" 44.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 5 Summe = 55.0" + System.lineSeparator() + 
			" 55.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 6 Summe = 66.0" + System.lineSeparator() + 
			" 66.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 7 Summe = 77.0" + System.lineSeparator() + 
			" 77.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 8 Summe = 88.0" + System.lineSeparator() + 
			" 88.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 9 Summe = 99.0" + System.lineSeparator() + 
			" 99.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 10 Summe = 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 11 Summe = 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Obj 12 Summe = 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0" + System.lineSeparator() + 
			" 0.0 0.0 0.0" + System.lineSeparator() + 
			"* Netzobjektnamen" + System.lineSeparator() + 
			"$NAMES" + System.lineSeparator() + 
			"10 \"0\"" + System.lineSeparator() + 
			"11 \"1\"" + System.lineSeparator() + 
			"12 \"2\"" + System.lineSeparator() + 
			"13 \"3\"" + System.lineSeparator() + 
			"14 \"4\"" + System.lineSeparator() + 
			"15 \"5\"" + System.lineSeparator() + 
			"16 \"6\"" + System.lineSeparator() + 
			"17 \"7\"" + System.lineSeparator() + 
			"18 \"8\"" + System.lineSeparator() + 
			"19 \"9\"" + System.lineSeparator() + 
			"20 \"10\"" + System.lineSeparator() + 
			"21 \"11\"" + System.lineSeparator() + 
			"22 \"12\"" + System.lineSeparator() + 
			" ";
}
