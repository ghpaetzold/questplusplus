package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/13/13
 */
public class Feature3004 extends Feature {
    public Feature3004() {
        setIndex(3004);
        this.addResource("target.mqm.abbreviation");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        Double qualityValue = (Double) target.getValue("abbrev_conflicts_divided_by_count");
        setValue(new Float(qualityValue));
    }
}
