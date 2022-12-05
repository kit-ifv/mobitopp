package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;

public enum StandardAttribute implements AttributeType {

	domCode("dom_code") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return createHouseholdAttributes(context, demography, HouseholdOfPanelData::domCode);
		}
	},
	householdSize("household_size") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return createHouseholdAttributes(context, demography, HouseholdOfPanelData::size);
		}
	},
	householdType("household_type") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return createHouseholdAttributes(context, demography, HouseholdOfPanelData::type);
		}
	},
	income("income") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return createHouseholdAttributes(context, demography, HouseholdOfPanelData::income);
		}
	},
	femaleAge("age_f") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return demography
					.femaleAge()
					.items()
					.map(item -> new FemaleAge(context, this, item.lowerBound(), item.upperBound(), item.amount()));
		}
	},
	maleAge("age_m") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return demography
					.maleAge()
					.items()
					.map(item -> new MaleAge(context, this, item.lowerBound(), item.upperBound(), item.amount()));
		}
	},
	employment("job") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return demography
                .employment()
                .items()
                .map(item -> new PersonAttribute(context, this, item.getTypeAsInt(),
                    item.getTypeAsInt(), item.amount(), PersonOfPanelData::getEmploymentTypeAsInt));

		}
	},
	graduation("graduation") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return createPersonAttributes(context, demography, person -> (int) person.getGenderTypeAsInt());
		}
	},
	commuterTicket("commuter_ticket") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return createPersonAttributes(context, demography, person -> person.hasCommuterTicket() ? 0:1);
		}
	},
	distance("distance") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return createPersonAttributes(context, demography, person -> (int) person.getPoleDistance());
		}
	},
	numberOfCars("number_of_cars") {

		@Override
		public Stream<Attribute> createAttributes(
				final Demography demography, final RegionalContext context) {
			return createHouseholdAttributes(context, demography, HouseholdOfPanelData::numberOfCars);
		}
	};

	private final String attributeName;

	private StandardAttribute(final String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	public String attributeName() {
		return attributeName;
	}

	@Override
	public String prefix() {
		return attributeName + ":";
	}

	@Override
	public String createInstanceName(final int lowerBound, final int upperBound) {
		return prefix() + lowerBound + "-" + upperBound;
	}

	@Override
	public String createInstanceName(final RangeDistributionItem item) {
		return createInstanceName(item.lowerBound(), item.upperBound());
	}

	Stream<Attribute> createPersonAttributes(
			final RegionalContext context, final Demography demography,
			final Function<PersonOfPanelData, Integer> valueOfPerson) {
		return demography
				.getDistribution(this)
				.items()
				.map(item -> createPersonAttribute(context, valueOfPerson, item));
	}

  private PersonAttribute createPersonAttribute(
      final RegionalContext context, final Function<PersonOfPanelData, Integer> personValue,
      final RangeDistributionItem item) {
    return new PersonAttribute(context, this, item.lowerBound(), item.upperBound(), item.amount(),
        personValue);
  }

	Stream<Attribute> createHouseholdAttributes(
			final RegionalContext context, final Demography demography,
			final Function<HouseholdOfPanelData, Integer> valueOfHousehold) {
		return demography
				.getDistribution(this)
				.items()
				.map(item -> createHouseholdAttribute(context, valueOfHousehold, item));
	}

	private DynamicHouseholdAttribute createHouseholdAttribute(
			final RegionalContext context,
			final Function<HouseholdOfPanelData, Integer> valueOfHousehold, final RangeDistributionItem item) {
    return new DynamicHouseholdAttribute(context, this, item.lowerBound(), item.upperBound(),
        item.amount(), valueOfHousehold);
	}

	@Override
	public abstract Stream<Attribute> createAttributes(
			final Demography demography, final RegionalContext context);
}