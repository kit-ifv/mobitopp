package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;

public class StructuralDataTest {

	private StructuralData demographyData;
	private StructuralData attractivityData;

	@Before
	public void initialise() throws URISyntaxException {
		demographyData = Example.demographyData();
		attractivityData = Example.attractivityData();
	}

	@Test
	public void getValue() {
		String first = demographyData.getValue("NAME");
		demographyData.next();
		String second = demographyData.getValue("NAME");
		demographyData.next();
		String third = demographyData.getValue("NAME");

		assertEquals("LB_Affalterbach (G)", first);
		assertEquals("LB_Asperg_West", second);
		assertEquals("LB_Asperg_Ost (G)", third);
	}

	@Test
	public void valueOrDefault() {
		int defaultValue = demographyData.valueOrDefault("Rundweg");
		demographyData.next();
		int existingValue = demographyData.valueOrDefault("Rundweg");

		assertEquals(0, defaultValue);
		assertEquals(8044, existingValue);
	}

	@Test
	public void classification() {
		ZoneClassificationType insideArea = attractivityData.currentClassification();
		attractivityData.next();
		ZoneClassificationType outsideArea = attractivityData.currentClassification();

		assertThat(insideArea, is(equalTo(ZoneClassificationType.areaOfInvestigation)));
		assertThat(outsideArea, is(equalTo(ZoneClassificationType.outlyingArea)));
	}

	@Test
	public void zoneAreaTypeFromString() {
		AreaType first = attractivityData.currentZoneAreaType();
		attractivityData.next();
		AreaType second = attractivityData.currentZoneAreaType();
		attractivityData.next();
		AreaType third = attractivityData.currentZoneAreaType();

		assertThat(first, is(equalTo(ZoneAreaType.CITYOUTSKIRT)));
		assertThat(second, is(equalTo(ZoneAreaType.CONURBATION)));
		assertThat(third, is(equalTo(ZoneAreaType.DEFAULT)));
	}
	
	@Test
	public void zoneAreaTypeFromCode() {
		StructuralData attractivityData = Example.attractivityDataByCode();
		AreaType first = attractivityData.currentZoneAreaType();
		attractivityData.next();
		AreaType second = attractivityData.currentZoneAreaType();
		attractivityData.next();
		AreaType third = attractivityData.currentZoneAreaType();

		assertThat(first, is(equalTo(ZoneAreaType.CITYOUTSKIRT)));
		assertThat(second, is(equalTo(ZoneAreaType.CONURBATION)));
		assertThat(third, is(equalTo(ZoneAreaType.DEFAULT)));
	}

	@Test
	public void zone() {
		int first = demographyData.currentZone();
		demographyData.next();
		int second = demographyData.currentZone();
		demographyData.next();
		int third = demographyData.currentZone();

		assertThat(first, is(equalTo(1)));
		assertThat(second, is(equalTo(2)));
		assertThat(third, is(equalTo(3)));
	}

	@Test
	public void hasNext() {
		boolean firstExists = demographyData.hasNext();
		demographyData.next();
		boolean secondExists = demographyData.hasNext();
		demographyData.next();
		boolean thirdExists = demographyData.hasNext();
		demographyData.next();
		boolean hasNoNext = demographyData.hasNext();

		assertTrue(firstExists);
		assertTrue(secondExists);
		assertTrue(thirdExists);
		assertFalse(hasNoNext);
	}

	@Test
	public void hasValue() {
		boolean hasNameValue = demographyData.hasValue("NAME");
		boolean hasNoPrivateVisitValue = demographyData.hasValue("Rundweg");

		assertTrue(hasNameValue);
		assertFalse(hasNoPrivateVisitValue);
	}

	@Test
	public void getAttributes() {
		List<String> attributes = demographyData.getAttributes();

		assertThat(attributes, contains("id", "name", "center:x", "center:y", "einwohner in der zone",
				"age:m:0-5", "age:m:6-9", "age:m:10-15", "age:m:16-18", "age:m:19-24", "age:m:25-29",
				"age:m:30-44", "age:m:45-59", "age:m:60-64", "age:m:65-74", "age:m:75-", "age:f:0-5",
				"age:f:6-9", "age:f:10-15", "age:f:16-18", "age:f:19-24", "age:f:25-29", "age:f:30-44",
				"age:f:45-59", "age:f:60-64", "age:f:65-74", "age:f:75-", "hhtyp:1", "hhtyp:2", "hhtyp:3",
				"hhtyp:4", "hhtyp:5", "hhtyp:6", "hhtyp:7", "hhtyp:8", "hhtyp:9", "hhtyp:10", "hhtyp:11",
				"hhtyp:12", "job:fulltime", "job:parttime", "job:none", "job:education_tertiary",
				"job:education_secondary", "job:education_primary", "job:education_occup", "job:retired",
				"job:infant", "arbeit qualifiziert", "arbeit einfach", "arbeit selbst�ndig",
				"arbeit teilzeit", "t�gl. bedarf", "sonst. bedarf", "erledigung", "besuche, fortb.",
				"restaurant, kultur", "sport, gr�nanl.", "bringen/holen", "grundschule", "weiterf. schule",
				"universit�t", "berufsschule", "rundweg"));
	}
}
