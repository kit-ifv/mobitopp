package edu.kit.ifv.mobitopp.data.local.configuration;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.time.Time;

public class DayTypeMatricesTest {

	private static final Time date = Data.someTime();
	private static final Time later = date.plusHours(1);
	private static final String path = "path";
	
	private DayTypeMatrices matrices;
	private TimeSpan firstSpan;
	private TimeSpan secondSpan;
	
	@Before
	public void initialise() {
		matrices = new DayTypeMatrices();
		firstSpan = new TimeSpan(0);
		secondSpan = new TimeSpan(1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void noMatricesSpecified() {
		matrices.setBetween(emptyMap());

		matrices.matchingTimeSpan(date);
	}
	
	@Test
	public void findTimeSpan() {
		matrices.setBetween(matrices());
		
		TimeSpan matchingFirstSpan = matrices.matchingTimeSpan(date);
		TimeSpan matchingSecondSpan = matrices.matchingTimeSpan(later);
		
		assertThat(matchingFirstSpan, is(equalTo(firstSpan)));
		assertThat(matchingSecondSpan, is(equalTo(secondSpan)));
	}

	private Map<TimeSpan, String> matrices() {
		Map<TimeSpan, String> paths = new HashMap<>();
		paths.put(firstSpan, path);
		paths.put(secondSpan, path);
		return paths;
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void missingTimeSpan() {
		matrices.in(firstSpan);
	}
	
	@Test
	public void bundleSpanWithMatrix() {
		TimeSpan rangeSpan = TimeSpan.between(0, 1);
		
		matrices.setBetween(matrices(rangeSpan));
		StoredMatrix matrix = matrices.in(firstSpan);
		
		StoredMatrix rangedMatrix = new StoredMatrix(rangeSpan, path);
		assertThat(matrix, is(equalTo(rangedMatrix )));
	}

	private Map<TimeSpan, String> matrices(TimeSpan rangeSpan) {
		return singletonMap(rangeSpan, path);
	}
	
	@Test
	public void coversWholeDay() {
		TimeSpan morning = TimeSpan.between(0, 11);
		TimeSpan afternoon = TimeSpan.between(12, 23);
		TimeSpan wholeDay = TimeSpan.between(0, 23);
		matrices.add(morning, path);
		matrices.add(afternoon, path);
		
		TimeSpan coveredTime = matrices.coveredTime();
		assertThat(coveredTime, is(wholeDay));
	}
	
	@Test
	public void coversPartOfTheDay() {
		TimeSpan partOfTheDay = TimeSpan.between(0, 1);
		matrices.setBetween(matrices());
		
		TimeSpan coveredTime = matrices.coveredTime();
		
		assertThat(coveredTime, is(equalTo(partOfTheDay)));
	}

}
