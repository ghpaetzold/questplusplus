package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.StringOperations;

public class BinQEFeature1006 extends Feature {

    public BinQEFeature1006() {
        setIndex(1006);
        setDescription("Size of the longest common subsequence.");
    }

    public void run(Sentence source, Sentence target) {
        setValue(StringOperations.lcs(source.getTokens(), target.getTokens()).size());
    }
}
