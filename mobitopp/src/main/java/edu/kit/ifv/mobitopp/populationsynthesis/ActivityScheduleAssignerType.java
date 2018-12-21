package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;

public enum ActivityScheduleAssignerType {

  actitopp() {

    @Override
    ActivityScheduleAssigner create(long seed, DataRepositoryForPopulationSynthesis dataRepository) {
      return new ActiToppScheduleCreator(seed);
    }

  },
  standard() {

    @Override
    ActivityScheduleAssigner create(long seed, DataRepositoryForPopulationSynthesis dataRepository) {
      return new DefaultActivityScheduleAssigner(new DefaultActivityScheduleCreator(),
          dataRepository.panelDataRepository());
    }

  };

  abstract ActivityScheduleAssigner create(
      long seed, DataRepositoryForPopulationSynthesis dataRepository);

}
