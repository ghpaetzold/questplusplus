package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.AlignmentData;
import shef.mt.features.util.Sentence;

public class BinQEFeature1012 extends Feature {

    public BinQEFeature1012() {
        setIndex(1012);
        setDescription("Normalized number of unaligned blocks.");
        addResource("alignments.file");
    }

    public void run(Sentence source, Sentence target) {
        AlignmentData ad = (AlignmentData) target.getValue("alignments.file");
        setValue((float)ad.getUnalignedBlocks().size()/(float)target.getNoTokens());
    }
}
