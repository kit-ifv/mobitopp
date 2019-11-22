package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

@ExtendWith(MockitoExtension.class)
public class OdPairSelectorDecoratorTest {

	@Mock
	private OdPairSelector other;
	@Mock
	private PersonBuilder person;
	@Mock
	private Community community;

	@Test
	void delegatesToOtherSelector() throws Exception {
		OdPairSelectorDecorator selector = new OdPairSelectorDecorator(other);

		selector.select(person);

		verify(other).select(person);
	}

	@Test
	void delegatesScaleToOtherSelector() throws Exception {
		int numberOfCommuters = 1;
		OdPairSelectorDecorator selector = new OdPairSelectorDecorator(other);

		selector.scale(community, numberOfCommuters);

		verify(other).scale(community, numberOfCommuters);
	}
}
