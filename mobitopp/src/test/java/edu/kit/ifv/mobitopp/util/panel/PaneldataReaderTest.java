package edu.kit.ifv.mobitopp.util.panel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaneldataReaderTest {

  private static final String exampleInput = "#ID;year;areatype;size;hhtype;notcontainedchildren;notcontainedchildrenmaxage;cars;pole;weight;personnumber;sex;graduation;birthyear;employmenttype;poledistance;commuterticket;hhincome;bicycle;caravailable;personalcar;mw4;mw5;d1;d2;d3;d4;d5;d6;d7;day;month;activitypattern"
      + System.lineSeparator()
      + "1;1996;2;2;6;0;10;2;1;1.44;1;1;1;1939;1;5;0;1500;1;1;1;0;0;1;2;2;2;3;4;5;14;10;-1;7;460;-1;15;1;275;475;12;7;278;-1;3;5;67;1043;3;7;837;-1;20;1;270;1970;20;7;82;-1;2;4;4;2344;2;7;10;-1;21;5;149;2381;30;7;785;-1;15;1;270;3360;15;7;120;-1;11;2;156;3776;8;7;30;-1;3;5;17;3973;50;5;25;-1;4;7;711;-1;15;1;325;4795;15;7;210;-1;30;4;130;5375;30;7;685;-1;15;1;275;6235;15;7;265;-1;5;4;15;6795;5;7;40;-1;3;5;147;6858;3;7;737;-1;20;4;165;7765;19;7;1336;-1;10;5;85;9295;10;7;690;-1"
      + System.lineSeparator();

  @BeforeEach
  public void initialise() {
  }

  @Test
  void reportsMissingColumns() throws Exception {
    PaneldataReader reader = createReaderFor(exampleInput);

    assertAll(() -> assertThat(reader.missingColumns(), hasEntry("gender", "sex")),
        () -> assertThat(reader.missingColumns(),
            hasEntry("licence", "caravailable and personalcar")),
        () -> assertThat(reader.missingColumns(), hasEntry("hhincome_class", "0")),
        () -> assertThat(reader.missingColumns(), hasEntry("incomeperson", "0")),
        () -> assertThat(reader.missingColumns(), hasEntry("pref_cardriver", "0.0")),
        () -> assertThat(reader.missingColumns(), hasEntry("pref_walking", "0.0")),
        () -> assertThat(reader.missingColumns(), hasEntry("pref_cycling", "0.0")),
        () -> assertThat(reader.missingColumns(), hasEntry("pref_publictransport", "0.0")));
  }

  private PaneldataReader createReaderFor(String input) {
    Reader reader;
    reader = new InputStreamReader(new ByteArrayInputStream(input.getBytes()));
    File file = new File("some");
    return new PaneldataReader(file) {

      @Override
      Reader readerFor(File file) throws IOException {
        return reader;
      }
    };
  }
}
