package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

public class FeatureBilingualTranslations extends Feature {

    public FeatureBilingualTranslations() {
        setIndex(6667);
    }

    @Override
    public void run(Sentence source, Sentence target) {
        Integer qualityValue = (Integer) target.getValue("bilingual-translations");
        setValue(new Float(qualityValue));
    }
}
