package shef.mt.features.impl.binqe;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.AlignmentData;
import shef.mt.features.util.Sentence;

public class BinQEFeature1014 extends Feature {

    public BinQEFeature1014() {
        setIndex(1014);
        setDescription("Modified Lesk score.");
        addResource("blockalignments");
    }

    public void run(Sentence source, Sentence target) {
        AlignmentData ad = (AlignmentData) target.getValue("blockalignments");
        float sum = 0;
        for(Integer[] block: ad.getAlignedBlocks()){
            float blocksize = block[1]-block[0];
            sum += blocksize*blocksize;
        }
        setValue(sum/(float)(source.getNoTokens()*target.getNoTokens()));
    }
}
