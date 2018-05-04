package edu.kit.ifv.mobitopp.panel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.kit.ifv.mobitopp.util.panel.PanelDataset;

public class PanelDatasetTest {

	private final PanelDataset ds_0_1999 = new PanelDataset(0, 1999);
	private final PanelDataset ds_0_2000 = new PanelDataset(0, 2000);
	private final PanelDataset ds_1_2000 = new PanelDataset(1, 2000);
	private final PanelDataset ds_1_2000_duplicate = new PanelDataset(1, 2000);

	@Test
	public void testNotEqual() {

		assertNotEquals(ds_0_1999,  ds_0_2000);
		assertNotEquals(ds_1_2000,  ds_0_2000);
	}

	@Test
	public void testEqual() {

		assertEquals(ds_1_2000,  ds_1_2000_duplicate);

		assertTrue(ds_1_2000.equals(ds_1_2000_duplicate));
	}

	@Test
	public void testHashCode() {

		assertEquals(ds_1_2000.hashCode(),  ds_1_2000_duplicate.hashCode());
	}


}
