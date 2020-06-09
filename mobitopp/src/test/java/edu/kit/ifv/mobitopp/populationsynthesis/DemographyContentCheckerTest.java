package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.zone;
import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute.distance;
import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute.domCode;
import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute.femaleAge;
import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute.householdSize;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
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
		when(data.attributes(any())).thenReturn(List.of());
	}

	@Test
	public void checkDemographyContainsAttributesOfType() {
		StructuralData communityData = Example.demographyData();
		StructuralData zoneData = Example.demographyData();
		when(data.attributes(community)).thenReturn(asList(femaleAge));
		when(data.attributes(zone)).thenReturn(asList(householdSize));
		when(data.get(community, femaleAge)).thenReturn(communityData);
		when(data.get(zone, householdSize)).thenReturn(zoneData);

		checker.verify(data);
	}

	@Test
	public void failsForMissingAttributeType() {
		when(data.attributes(community)).thenReturn(List.of(domCode));
		when(data.attributes(zone)).thenReturn(List.of(distance));
		when(data.get(community, domCode)).thenReturn(Example.demographyData());
		when(data.get(zone, distance)).thenReturn(Example.demographyData());

		assertThrows(IllegalArgumentException.class, () -> checker.verify(data));
	}

	@Test
	public void calculateMissingAttributes() {
		when(data.attributes(community)).thenReturn(List.of(domCode));
		when(data.attributes(zone)).thenReturn(asList(distance, femaleAge));
		StructuralData structuralData = Example.demographyData();
		when(data.get(community, domCode)).thenReturn(structuralData);
		when(data.get(zone, distance)).thenReturn(structuralData);
		when(data.get(zone, femaleAge)).thenReturn(structuralData);

		List<AttributeType> missingAttributes = checker.calculateMissingAttributes(data);

		assertThat(missingAttributes).contains(StandardAttribute.domCode, StandardAttribute.distance);
	}

}
