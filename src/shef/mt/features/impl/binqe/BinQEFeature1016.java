package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.tercom.TERalignment;

public class BinQEFeature1016 extends Feature {

    public BinQEFeature1016() {
        setIndex(1016);
        setDescription("TER score.");
        addResource("teralignment");
    }

    public void run(Sentence source, Sentence target) {
        //Get TER alignment:
        TERalignment t = (TERalignment) target.getValue("teralignment");
        
        //Set value:
        setValue((float) t.score());
    }
}
