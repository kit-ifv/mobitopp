package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.populationsynthesis.region.MultipleZones;

@ExtendWith(MockitoExtension.class)
public class MultiLevelIterationFactoryTest {

	@Mock
	private SynthesisContext context;

	private Demography regionDemography;
	private DemandZone somePart;
	private DemandZone otherPart;
	private Demography somePartDemography;
	private Demography otherPartDemography;

	@BeforeEach
	private void initialise() {
		ExampleDemandZones zonesToCopyFrom = ExampleDemandZones.create();
		Map<AttributeType, RangeDistributionIfc> regionDistributions = Map
				.of(StandardAttribute.householdSize,
						zonesToCopyFrom
								.someZone()
								.nominalDemography()
								.getDistribution(StandardAttribute.householdSize));
		Map<AttributeType, RangeDistributionIfc> somePartDistributions = Map
				.of(StandardAttribute.femaleAge,
						zonesToCopyFrom
								.someZone()
								.nominalDemography()
								.getDistribution(StandardAttribute.femaleAge));
		Map<AttributeType, RangeDistributionIfc> otherPartDistributions = Map
				.of(StandardAttribute.femaleAge,
						zonesToCopyFrom
						.otherZone()
						.nominalDemography()
						.getDistribution(StandardAttribute.femaleAge));
		regionDemography = new Demography(EmploymentDistribution.createDefault(),
				regionDistributions);
		somePartDemography = new Demography(EmploymentDistribution.createDefault(), somePartDistributions);
		somePart = new DemandZone(zonesToCopyFrom.someZone().zone(), somePartDemography);
		otherPartDemography = new Demography(EmploymentDistribution.createDefault(), otherPartDistributions);
		otherPart = new DemandZone(zonesToCopyFrom.otherZone().zone(), otherPartDemography);
		when(context.attributes(community))
				.thenReturn(List.of(StandardAttribute.householdSize));
		when(context.attributes(RegionalLevel.zone)).thenReturn(List.of(StandardAttribute.femaleAge));
	}

	@Test
	void buildsUpAttributesOfAllLevels() throws Exception {
		List<DemandZone> zones = List.of(somePart, otherPart);
		DemandRegion region = new MultipleZones("1", community, regionDemography, zones);
		BaseIterationFactory builder = new MultiLevelIterationFactory(context);

		List<Attribute> attributes = builder.attributesFor(region).collect(toList());

		assertThat(attributes).containsOnlyElementsOf(multiLevelAttributes(region, zones));
	}

	private List<Attribute> multiLevelAttributes(DemandRegion region, List<DemandZone> zones) {
		List<Attribute> attributes = new ArrayList<>();
		attributes
				.addAll(householdAttributeFor(region.nominalDemography(),
						region.getRegionalContext()));
		for (DemandZone zone : zones) {
			attributes.addAll(femaleAttributesFor(zone.nominalDemography(), zone.getRegionalContext()));
		}
		return attributes;
	}

	private List<Attribute> femaleAttributesFor(
			Demography nominalDemography, RegionalContext regionalContext) {
		return StandardAttribute.femaleAge
				.createAttributes(nominalDemography, regionalContext)
				.collect(toList());
	}

	private List<Attribute> householdAttributeFor(Demography nominalDemography, RegionalContext regionalContext) {
		return StandardAttribute.householdSize
				.createAttributes(nominalDemography, regionalContext)
				.collect(toList());
	}

	@Test
	void buildsUpAttributesForEachZone() throws Exception {
		List<DemandZone> zones = List.of(somePart, otherPart);
		DemandRegion region = new MultipleZones("1", community, regionDemography, zones);
		List<Attribute> someAttributes = new LinkedList<>();
		someAttributes.addAll(householdAttributeFor(regionDemography, region.getRegionalContext()));
		someAttributes
				.addAll(femaleAttributesFor(somePart.nominalDemography(), somePart.getRegionalContext()));
		List<Attribute> otherAttributes = new LinkedList<>();
		otherAttributes.addAll(householdAttributeFor(regionDemography, region.getRegionalContext()));
		otherAttributes
				.addAll(femaleAttributesFor(otherPart.nominalDemography(), otherPart.getRegionalContext()));
		MultiLevelIterationFactory builder = new MultiLevelIterationFactory(context);

		Map<RegionalContext, List<Attribute>> attributes = builder.attributesPerZone(region);

		assertThat(attributes)
				.containsEntry(somePart.getRegionalContext(), someAttributes)
				.containsEntry(otherPart.getRegionalContext(), otherAttributes);
	}
}
