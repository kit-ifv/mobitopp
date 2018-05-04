package edu.kit.ifv.mobitopp.time;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
	public void minus() {
		RelativeTime some = RelativeTime.ofSeconds(1);
		RelativeTime another = RelativeTime.ofSeconds(1);
		
		RelativeTime sum = some.minus(another);
		
		assertThat(sum, is(RelativeTime.ofSeconds(0)));
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(RelativeTime.class).usingGetClass().verify();
	}
}
