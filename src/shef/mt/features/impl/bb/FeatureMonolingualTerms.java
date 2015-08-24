package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

public class FeatureMonolingualTerms extends Feature {

    public FeatureMonolingualTerms() {
        setIndex(6666);
    }

    @Override
    public void run(Sentence source, Sentence target) {
        Integer qualityValue = (Integer) target.getValue("monolingual-terms");
        setValue(new Float(qualityValue));
    }
}
