package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.AlignmentData;
import shef.mt.features.util.Sentence;

public class BinQEFeature1013 extends Feature {

    public BinQEFeature1013() {
        setIndex(1013);
        setDescription("Normalized density difference.");
        addResource("alignments.file");
    }

    public void run(Sentence source, Sentence target) {
        AlignmentData ad = (AlignmentData) target.getValue("alignments.file");
        double adensity = (float)ad.getTargetAligned().size()/(float)ad.getAlignedBlocks().size();
        double udensity = (float)ad.getTargetUnaligned().size()/(float)ad.getAlignedBlocks().size();
        setValue((float)(adensity-udensity)/(float)target.getNoTokens());
    }
}
