package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * the number of words that appears in the slang dictionary
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/11/13
 */
public class Feature3001 extends Feature {
    public Feature3001() {
        setIndex(3001);
        this.addResource("target.mqm.slang");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        Integer qualityValue = (Integer) target.getValue("slang_words_count");
        setValue(new Float(qualityValue));
    }
}
