package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.AlignmentData;
import shef.mt.features.util.Sentence;

public class BinQEFeature1011 extends Feature {

    public BinQEFeature1011() {
        setIndex(1011);
        setDescription("Normalized number of aligned blocks.");
        addResource("alignments.file");
    }

    public void run(Sentence source, Sentence target) {
        AlignmentData ad = (AlignmentData) target.getValue("alignments.file");
        setValue((float)ad.getAlignedBlocks().size()/(float)target.getNoTokens());
    }
}
