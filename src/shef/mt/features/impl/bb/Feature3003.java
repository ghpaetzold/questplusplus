package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/13/13
 */
public class Feature3003 extends Feature {
    public Feature3003() {
        setIndex(3003);
        this.addResource("target.mqm.abbreviation");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        Integer qualityValue = (Integer) target.getValue("abbrev_conflicts");
        setValue(new Float(qualityValue));
    }
}
