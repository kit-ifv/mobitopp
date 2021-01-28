package edu.kit.ifv.mobitopp.data.local.configuration;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ParsingTestData {
	String a;
	String b;
	String c;
	List<Integer> list;
	ParsingTestData nest;
}
