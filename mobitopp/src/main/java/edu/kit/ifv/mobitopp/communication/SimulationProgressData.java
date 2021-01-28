package edu.kit.ifv.mobitopp.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SimulationProgressData {
	
	private int simulation_second;
	private int simulation_end;

}
