package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.StringOperations;

public class BinQEFeature1007 extends Feature {

    public BinQEFeature1007() {
        setIndex(1007);
        setDescription("Size of the longest common subsequence normalized by source size.");
    }

    public void run(Sentence source, Sentence target) {
        setValue((float)StringOperations.lcs(source.getTokens(), target.getTokens()).size()/(float)source.getNoTokens());
    }
}
