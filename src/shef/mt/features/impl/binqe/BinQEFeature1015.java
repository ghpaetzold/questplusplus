package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.tercom.TERalignment;

public class BinQEFeature1015 extends Feature {

    public BinQEFeature1015() {
        setIndex(1015);
        setDescription("TER number of edits.");
        addResource("teralignment");
    }

    public void run(Sentence source, Sentence target) {
        //Get TER alignment:
        TERalignment t = (TERalignment) target.getValue("teralignment");
        
        //Set value:
        setValue((float) t.numEdits);
    }
}
