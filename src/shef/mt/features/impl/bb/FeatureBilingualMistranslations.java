package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

public class FeatureBilingualMistranslations extends Feature {

    public FeatureBilingualMistranslations() {
        setIndex(6668);
    }

    @Override
    public void run(Sentence source, Sentence target) {
        Integer qualityValue = (Integer) target.getValue("bilingual-mistranslations");
        setValue(new Float(qualityValue));
    }
}
