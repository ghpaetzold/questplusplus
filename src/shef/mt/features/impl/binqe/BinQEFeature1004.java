package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

public class BinQEFeature1004 extends Feature {

    public BinQEFeature1004() {
        setIndex(1004);
        setDescription("Number of source words.");
    }

    public void run(Sentence source, Sentence target) {
        setValue(source.getNoTokens());
    }
}
