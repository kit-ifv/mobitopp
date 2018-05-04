package edu.kit.ifv.mobitopp.simulation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import edu.kit.ifv.mobitopp.time.Time;

public class HooksTest {

	@Test
	public void processesAllHooks() throws Exception {
		Hooks hooks = new Hooks();
		Hook first = mock(Hook.class);
		Hook second = mock(Hook.class);
		Time time = mock(Time.class);

		hooks.add(first);
		hooks.add(second);
		hooks.process(time);

		verify(first).process(time);
		verify(second).process(time);
	}

}
