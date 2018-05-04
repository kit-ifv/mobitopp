package edu.kit.ifv.mobitopp.populationsynthesis;

public enum ActivityScheduleCreatorType {

	actitopp() {

		@Override
		ActivityScheduleCreator create(long seed) {
			return new ActiToppScheduleCreator(seed);
		}

	},
	standard() {

		@Override
		ActivityScheduleCreator create(long seed) {
			return new DefaultActivityScheduleCreator();
		}

	};

	abstract ActivityScheduleCreator create(long seed);

}
