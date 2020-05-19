package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.community.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

@ExtendWith(MockitoExtension.class)
public class DemographyContentCheckerTest {

	@Mock
	private DemographyData data;
	private DemographyContentChecker checker;

	@BeforeEach
	public void initialise() {
		checker = new DemographyContentChecker();
	}

	@Test
	public void checkDemographyContainsAttributesOfType() {
		StructuralData communityData = Example.demographyData();
		StructuralData zoneData = Example.demographyData();
		when(data.attributes(RegionalLevel.community)).thenReturn(asList(StandardAttribute.femaleAge));
		when(data.attributes(RegionalLevel.zone)).thenReturn(asList(StandardAttribute.householdSize));
		when(data.get(RegionalLevel.community, StandardAttribute.femaleAge)).thenReturn(communityData);
		when(data.get(RegionalLevel.zone, StandardAttribute.householdSize)).thenReturn(zoneData);

		checker.verify(data);
	}

	@Test
	public void failsForMissingAttributeType() {
		when(data.attributes(RegionalLevel.community)).thenReturn(List.of(StandardAttribute.domCode));
		when(data.attributes(RegionalLevel.zone)).thenReturn(List.of(StandardAttribute.distance));
		when(data.get(RegionalLevel.community, StandardAttribute.domCode))
				.thenReturn(Example.demographyData());
		when(data.get(RegionalLevel.zone, StandardAttribute.distance))
				.thenReturn(Example.demographyData());

		assertThrows(IllegalArgumentException.class, () -> checker.verify(data));
	}

	@Test
	public void calculateMissingAttributes() {
		when(data.attributes(RegionalLevel.community)).thenReturn(List.of(StandardAttribute.domCode));
		when(data.attributes(RegionalLevel.zone))
				.thenReturn(asList(StandardAttribute.distance, StandardAttribute.femaleAge));
		StructuralData structuralData = Example.demographyData();
		when(data.get(RegionalLevel.community, StandardAttribute.domCode)).thenReturn(structuralData);
		when(data.get(RegionalLevel.zone, StandardAttribute.distance)).thenReturn(structuralData);
		when(data.get(RegionalLevel.zone, StandardAttribute.femaleAge)).thenReturn(structuralData);

		List<AttributeType> missingAttributes = checker.calculateMissingAttributes(data);

		assertThat(missingAttributes).contains(StandardAttribute.domCode, StandardAttribute.distance);
	}

}
