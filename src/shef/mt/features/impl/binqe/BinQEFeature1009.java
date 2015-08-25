package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.AlignmentData;
import shef.mt.features.util.Sentence;

public class BinQEFeature1009 extends Feature {

    public BinQEFeature1009() {
        setIndex(1009);
        setDescription("Aligned word density.");
        addResource("alignments.file");
    }

    public void run(Sentence source, Sentence target) {
        AlignmentData ad = (AlignmentData) target.getValue("alignments.file");
        setValue((float)ad.getTargetAligned().size()/(float)ad.getAlignedBlocks().size());
    }
}
