package edu.kit.ifv.mobitopp.time;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.temporal.ChronoUnit;

import org.junit.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;
import nl.jqno.equalsverifier.EqualsVerifier;

public class RelativeTimeTest {

	@Test
	public void madeUpOfSeconds() throws Exception {
		RelativeTime time = RelativeTime.of(1l, SECONDS);

		assertThat(time.seconds(), is(1l));
	}

	@Test
	public void oneMinuteInSeconds() throws Exception {
		RelativeTime time = RelativeTime.of(1l, ChronoUnit.MINUTES);

		assertThat(time.seconds(), is(60l));
	}

	@Test
	public void oneHourInSeconds() throws Exception {
		RelativeTime time = RelativeTime.of(1l, ChronoUnit.HOURS);

		assertThat(time.seconds(), is(3600l));
	}
	
	@Test
	public void plus() {
		RelativeTime some = RelativeTime.ofSeconds(1);
		RelativeTime another = RelativeTime.ofSeconds(1);
		
		RelativeTime sum = some.plus(another);
		
		assertThat(sum, is(RelativeTime.ofSeconds(2)));
	}

	@Test
	public void plusWeeks() {
		int weeks = 1;
		RelativeTime some = RelativeTime.ofWeeks(weeks);
		
		RelativeTime difference = some.plusWeeks(weeks);
		
		assertThat(difference, is(RelativeTime.ofWeeks(2)));
	}
	
	@Test
	public void minus() {
		RelativeTime some = RelativeTime.ofSeconds(1);
		RelativeTime another = RelativeTime.ofSeconds(1);
		
		RelativeTime difference = some.minus(another);
		
		assertThat(difference, is(RelativeTime.ofSeconds(0)));
	}
	
	@Test
	public void minusWeeks() {
		int weeks = 1;
		RelativeTime some = RelativeTime.ofWeeks(weeks);
		
		RelativeTime difference = some.minusWeeks(weeks);
		
		assertThat(difference, is(RelativeTime.ZERO));
	}
	
	@Test
	public void multiply() {
		RelativeTime some = RelativeTime.ofSeconds(1);
		RelativeTime another = RelativeTime.ofSeconds(2);
		
		RelativeTime product = some.multiplyBy(2.0d);
		
		assertThat(product, is(equalTo(another)));
	}
	
	@Test
	public void isNegative() {
		RelativeTime time = RelativeTime.ofSeconds(-1);
		
		assertTrue(time.isNegative());
	}
	
	@Test
	public void isPositive() {
		RelativeTime time = RelativeTime.ofSeconds(1);
		
		assertFalse(time.isNegative());
	}
	
	@Test
	public void toFirstWeek() {
		RelativeTime week = RelativeTime.ofDays(6);
		
		long weeks = week.toWeeks();
		
		assertThat(weeks, is(0l));
	}
	
	@Test
	public void toSecondWeek() {
		RelativeTime week = RelativeTime.ofDays(7);
		
		long weeks = week.toWeeks();
		
		assertThat(weeks, is(1l));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(RelativeTime.class).usingGetClass().verify();
	}
}
