package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

public class BinQEFeature1005 extends Feature {

    public BinQEFeature1005() {
        setIndex(1005);
        setDescription("Number of target words.");
    }

    public void run(Sentence source, Sentence target) {
        setValue(target.getNoTokens());
    }
}
