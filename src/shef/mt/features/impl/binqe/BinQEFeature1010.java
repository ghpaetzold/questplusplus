package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.AlignmentData;
import shef.mt.features.util.Sentence;

public class BinQEFeature1010 extends Feature {

    public BinQEFeature1010() {
        setIndex(1010);
        setDescription("Unaligned word density.");
        addResource("alignments.file");
    }

    public void run(Sentence source, Sentence target) {
        AlignmentData ad = (AlignmentData) target.getValue("alignments.file");
        setValue((float)ad.getTargetUnaligned().size()/(float)ad.getAlignedBlocks().size());
    }
}
