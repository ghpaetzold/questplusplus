package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.StringOperations;

public class BinQEFeature1008 extends Feature {

    public BinQEFeature1008() {
        setIndex(1008);
        setDescription("Size of the longest common subsequence normalized by target size.");
    }

    public void run(Sentence source, Sentence target) {
        setValue((float)StringOperations.lcs(source.getTokens(), target.getTokens()).size()/(float)target.getNoTokens());
    }
}
