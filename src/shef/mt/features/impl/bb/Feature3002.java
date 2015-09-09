package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * Target sentence slang_words_count/num_of_words
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/11/13
 */
public class Feature3002 extends Feature {
    public Feature3002() {
        setIndex(3002);
        this.addResource("target.mqm.slang");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        if (target.getNoTokens() > 0) {
            Integer qualityValue = (Integer) target.getValue("slang_words_count");
            setValue(new Float(qualityValue) / target.getNoTokens());
        } else {
            setValue(0);
        }
    }
}
