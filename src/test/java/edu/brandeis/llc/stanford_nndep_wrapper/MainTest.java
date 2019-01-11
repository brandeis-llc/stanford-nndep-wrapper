package edu.brandeis.llc.stanford_nndep_wrapper;

import org.junit.Test;

import java.io.ByteArrayInputStream;

public class MainTest {


    @Test
    public void testTaggedInput() throws Exception {
        String samplePOS = "The/DT Fulton_County_Grand_Jury/NNP said/VB Friday/NN an/DT investigation/NN of/IN Atlanta/NN 's/POS recent/JJ primary_election/NN produced/VB `/` no/DT evidence/NN '/' that/IN any/DT irregularities/NN took_place/VB ./.";
        System.setIn(new ByteArrayInputStream(samplePOS.getBytes()));
        Main.main(new String[0]);
    }
    @Test
    public void testTaggedLemmatizedInput() throws Exception {
        String sampleLemmaPOS = "The/the/DT Fulton_County_Grand_Jury/fcgj/NNP said/say/VB Friday/friday/NN an/a/DT investigation/investigae/NN of/of/IN Atlanta/atlanta/NN 's/s/POS recent/recent/JJ primary_election/pe/NN produced/produce/VB `/`/` no/no/DT evidence/evidence/NN '/'/' that/that/IN any/a/DT irregularities/irregularity/NN took_place/take_place/VB ././PUNC";
        System.setIn(new ByteArrayInputStream(sampleLemmaPOS.getBytes()));
        Main.main(new String[0]);
    }
    @Test
    public void testMultilineInput() throws Exception {
        String sampleMultiline = "The/DT Fulton_County_Grand_Jury/NNP said/VB Friday/NN an/DT investigation/NN of/IN Atlanta/NN 's/POS recent/JJ primary_election/NN produced/VB `/` no/DT evidence/NN '/' that/IN any/DT irregularities/NN took_place/VB ./." +
                "\nThe/the/DT Fulton_County_Grand_Jury/fcgj/NNP said/say/VB Friday/friday/NN an/a/DT investigation/investigae/NN of/of/IN Atlanta/atlanta/NN 's/s/POS recent/recent/JJ primary_election/pe/NN produced/produce/VB `/`/` no/no/DT evidence/evidence/NN '/'/' that/that/IN any/a/DT irregularities/irregularity/NN took_place/take_place/VB ././PUNC";
        System.setIn(new ByteArrayInputStream(sampleMultiline.getBytes()));
        Main.main(new String[0]);
    }
    @Test
    public void testTilderDelimitedInput() throws Exception {
        String sampleMultilineTilde = "The~DT Fulton_County_Grand_Jury~NNP said~VB Friday~NN an~DT investigation~NN of~IN Atlanta~NN 's~POS recent~JJ primary_election~NN produced~VB `~` no~DT evidence~NN '~' that~IN any~DT irregularities~NN took_place~VB .~." +
                "\nThe~the~DT Fulton_County_Grand_Jury~fcgj~NNP said~say~VB Friday~friday~NN an~a~DT investigation~investigae~NN of~of~IN Atlanta~atlanta~NN 's~s~POS recent~recent~JJ primary_election~pe~NN produced~produce~VB `~`~` no~no~DT evidence~evidence~NN '~'~' that~that~IN any~a~DT irregularities~irregularity~NN took_place~take_place~VB .~.~PUNC";
        System.setIn(new ByteArrayInputStream(sampleMultilineTilde.getBytes()));
        Main.main(new String[]{"-d", "~"});
    }

    @Test(expected = Exception.class)
    public void testInvalidInput() throws Exception {
        String invalid = "Karen/NNP flew/VBP to/TO New York/NNP ./PUNC";
        System.setIn(new ByteArrayInputStream(invalid.getBytes()));
        Main.main(new String[0]);

    }

}